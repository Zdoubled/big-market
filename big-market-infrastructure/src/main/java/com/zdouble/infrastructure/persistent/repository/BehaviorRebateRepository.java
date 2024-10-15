package com.zdouble.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.zdouble.domain.rebate.model.aggregate.UserBehaviorRebateAggregate;
import com.zdouble.domain.rebate.model.entity.DailyBehaviorRebateEntity;
import com.zdouble.domain.rebate.model.entity.TaskEntity;
import com.zdouble.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.zdouble.domain.rebate.model.vo.BehaviorRebateState;
import com.zdouble.domain.rebate.model.vo.BehaviorTypeVO;
import com.zdouble.domain.rebate.model.vo.RebateTypeVO;
import com.zdouble.domain.rebate.repository.IBehaviorRebateRepository;
import com.zdouble.infrastructure.event.EventPublisher;
import com.zdouble.infrastructure.persistent.dao.DailyBehaviorRebateDao;
import com.zdouble.infrastructure.persistent.dao.TaskDao;
import com.zdouble.infrastructure.persistent.dao.UserBehaviorRebateOrderDao;
import com.zdouble.infrastructure.persistent.po.DailyBehaviorRebate;
import com.zdouble.infrastructure.persistent.po.Task;
import com.zdouble.infrastructure.persistent.po.UserBehaviorRebateOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class BehaviorRebateRepository implements IBehaviorRebateRepository {

    @Resource
    private DailyBehaviorRebateDao dailyBehaviorRebateDao;
    @Resource
    private UserBehaviorRebateOrderDao userBehaviorRebateOrderDao;
    @Resource
    private TaskDao taskDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private EventPublisher eventPublisher;;

    @Override
    public List<DailyBehaviorRebateEntity> queryDailyBehaviorRebateByBehaviorType(String behaviorType) {
        List<DailyBehaviorRebate> dailyBehaviorRebates = dailyBehaviorRebateDao.queryDailyBehaviorRebateByBehaviorType(behaviorType);
        return dailyBehaviorRebates.stream().map(dailyBehaviorRebate -> {
            return DailyBehaviorRebateEntity.builder()
                    .rebateConfig(dailyBehaviorRebate.getRebateConfig())
                    .rebateDesc(dailyBehaviorRebate.getRebateDesc())
                    .rebateType(RebateTypeVO.valueOf(dailyBehaviorRebate.getRebateType()))
                    .state(BehaviorRebateState.valueOf(dailyBehaviorRebate.getState()))
                    .behaviorType(BehaviorTypeVO.valueOf(dailyBehaviorRebate.getBehaviorType()))
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public void insertUserBehaviorRebateAggregates(String userId, List<UserBehaviorRebateAggregate> userBehaviorRebateAggregates) {
        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                for (UserBehaviorRebateAggregate userBehaviorRebateAggregate : userBehaviorRebateAggregates) {
                    TaskEntity taskEntity = userBehaviorRebateAggregate.getTaskEntity();
                    UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity = userBehaviorRebateAggregate.getUserBehaviorRebateOrder();
                    // 1. 创建task对象
                    Task task = Task.builder()
                            .topic(taskEntity.getTopic())
                            .userId(taskEntity.getUserId())
                            .message(JSON.toJSONString(taskEntity.getMessage()))
                            .messageId(taskEntity.getMessageId())
                            .state(taskEntity.getState().getCode())
                            .build();
                    // 2. 创建order对象
                    UserBehaviorRebateOrder userBehaviorRebateOrder = UserBehaviorRebateOrder.builder()
                            .userId(userBehaviorRebateOrderEntity.getUserId())
                            .orderId(userBehaviorRebateOrderEntity.getOrderId())
                            .bizId(userBehaviorRebateOrderEntity.getBizId())
                            .outBusinessNo(userBehaviorRebateOrderEntity.getOutBusinessNo())
                            .behaviorType(userBehaviorRebateOrderEntity.getBehaviorType().getCode())
                            .rebateConfig(userBehaviorRebateOrderEntity.getRebateConfig())
                            .rebateType(userBehaviorRebateOrderEntity.getRebateType().getCode())
                            .rebateDesc(userBehaviorRebateOrderEntity.getRebateDesc())
                            .build();
                    try {
                        taskDao.insertTask(task);
                        userBehaviorRebateOrderDao.insertUserBehaviorRebateOrder(userBehaviorRebateOrder);
                    }catch (DuplicateKeyException e){
                        log.info("用户行为返利重复插入，userId:{},behaviorType:{}", userId, userBehaviorRebateOrder.getBehaviorType());
                        status.setRollbackOnly();
                    }
                }
                return 1;
            });
        }finally {
            dbRouter.clear();
        }
        // 等待任务和订单落库后发送mq消息
        for (UserBehaviorRebateAggregate userBehaviorRebateAggregate : userBehaviorRebateAggregates) {
            TaskEntity taskEntity = userBehaviorRebateAggregate.getTaskEntity();
            Task task = Task.builder().userId(taskEntity.getUserId()).messageId(taskEntity.getMessageId()).build();
            try {
                eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                taskDao.updateTaskStateCompleted(task);
            }catch (Exception e){
                log.info("用户行为返利发送mq消息失败", e);
                taskDao.updateTaskStateFail(task);
            }
        }
    }

    @Override
    public List<UserBehaviorRebateOrderEntity> queryUserBehaviorRebateOrder(UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity) {
        UserBehaviorRebateOrder userBehaviorRebateOrder = UserBehaviorRebateOrder.builder()
                .userId(userBehaviorRebateOrderEntity.getUserId())
                .behaviorType(userBehaviorRebateOrderEntity.getBehaviorType().getCode())
                .outBusinessNo(userBehaviorRebateOrderEntity.getOutBusinessNo())
                .build();
        List<UserBehaviorRebateOrder> userBehaviorRebateOrders = userBehaviorRebateOrderDao.queryUserBehaviorRebateOrder(userBehaviorRebateOrderEntity);
        return userBehaviorRebateOrders.stream().map(rebateOrder -> {
            return UserBehaviorRebateOrderEntity.builder()
                    .userId(rebateOrder.getUserId())
                    .orderId(rebateOrder.getOrderId())
                    .behaviorType(BehaviorTypeVO.valueOf(rebateOrder.getBehaviorType()))
                    .rebateConfig(rebateOrder.getRebateConfig())
                    .rebateDesc(rebateOrder.getRebateDesc())
                    .rebateType(RebateTypeVO.valueOf(rebateOrder.getRebateType()))
                    .outBusinessNo(rebateOrder.getOutBusinessNo())
                    .bizId(rebateOrder.getBizId())
                    .build();
        }).collect(Collectors.toList());
    }
}
