package com.zdouble.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.zdouble.domain.award.aggregrate.UserAwardRecordAggregate;
import com.zdouble.domain.award.event.UserAwardSendMessageEvent;
import com.zdouble.domain.award.model.entity.TaskEntity;
import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;
import com.zdouble.domain.award.model.vo.TaskStateVO;
import com.zdouble.domain.award.reporsitory.IAwardRecordRepository;
import com.zdouble.infrastructure.event.EventPublisher;
import com.zdouble.infrastructure.persistent.dao.TaskDao;
import com.zdouble.infrastructure.persistent.dao.UserAwardRecordDao;
import com.zdouble.infrastructure.persistent.dao.UserRaffleOrderDao;
import com.zdouble.infrastructure.persistent.po.Task;
import com.zdouble.infrastructure.persistent.po.UserAwardRecord;
import com.zdouble.infrastructure.persistent.po.UserRaffleOrder;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Repository
@Slf4j
public class AwardRecordRepository implements IAwardRecordRepository {

    @Value("${spring.rabbitmq.topic.award_send}")
    private String topic;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Resource
    private UserAwardRecordDao userAwardRecordDao;
    @Resource
    private UserRaffleOrderDao userRaffleOrderDao;
    @Resource
    private TaskDao taskDao;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private IDBRouterStrategy routerStrategy;
    @Resource
    private ThreadPoolExecutor executor;


    @Override
    public void insertAwardRecordAndTask(UserAwardRecordAggregate userAwardRecordAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        String userId = taskEntity.getUserId();
        // 1. 构建task对象
        Task task = Task.builder()
                .userId(userId)
                .topic(taskEntity.getTopic())
                .state(taskEntity.getState().getCode())
                .message(JSON.toJSONString(taskEntity.getMessage()))
                .messageId(taskEntity.getMessageId())
                .topic(taskEntity.getTopic())
                .build();

        // 2. 构建userAwardRecord对象
        UserAwardRecord userAwardRecord = UserAwardRecord.builder()
                .userId(userAwardRecordEntity.getUserId())
                .strategyId(userAwardRecordEntity.getStrategyId())
                .orderId(userAwardRecordEntity.getOrderId())
                .activityId(userAwardRecordEntity.getActivityId())
                .awardId(userAwardRecordEntity.getAwardId())
                .awardState(userAwardRecordEntity.getAwardState().getCode())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .awardTime(userAwardRecordEntity.getAwardTime())
                .build();
        // 3. 事务执行task、userAwardRecord落库,更新订单状态
        UserRaffleOrder userRaffleOrder = UserRaffleOrder.builder()
                .userId(userAwardRecordEntity.getUserId())
                .orderId(userAwardRecordEntity.getOrderId())
                .build();
        try {
            routerStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    userAwardRecordDao.insertUserAwardRecord(userAwardRecord);
                    taskDao.insertTask(task);
                    int count = userRaffleOrderDao.updateUserRaffleOrderStateUsed(userRaffleOrder);
                    if (1 != count) {
                        status.setRollbackOnly();
                        throw new AppException(ResponseCode.USER_ORDER_USED_ERROR.getCode(), ResponseCode.USER_ORDER_USED_ERROR.getInfo());
                    }
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
                return 1;
            });
        } finally {
            routerStrategy.clear();
        }
        // 4. 发送奖品发放mq
        executor.execute(() ->{
            try {
                eventPublisher.publish(topic, taskEntity.getMessage());
                taskDao.updateTaskStateCompleted(task);
                log.info("发送奖品发放mq成功");
            } catch (Exception e) {
                taskDao.updateTaskStateFail(task);
                log.info("发送奖品发放mq失败");
            }
        });
    }
}
