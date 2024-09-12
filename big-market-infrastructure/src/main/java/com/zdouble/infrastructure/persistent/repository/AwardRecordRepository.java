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
import com.zdouble.infrastructure.persistent.po.Task;
import com.zdouble.infrastructure.persistent.po.UserAwardRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

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
    private TaskDao taskDao;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private IDBRouterStrategy routerStrategy;


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
        // 3. 事务执行task、userAwardRecord落库
        try {
            routerStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    userAwardRecordDao.insertUserAwardRecord(userAwardRecord);
                    taskDao.insertTask(task);
                }catch (DuplicateKeyException e){
                    log.error("事务执行task、userAwardRecord落库失败, 唯一键冲突", e);
                    status.setRollbackOnly();
                }
                return 1;
            });
        } finally {
            routerStrategy.clear();
        }
        // 4. 发送奖品发放mq
        try{
            eventPublisher.publish(topic, taskEntity.getMessage());
            taskDao.updateTaskStateCompleted(task);
            log.info("发送奖品发放mq成功");
        }catch (Exception e){
            taskDao.updateTaskStateFail(task);
            log.info("发送奖品发放mq失败");
        }
    }
}
