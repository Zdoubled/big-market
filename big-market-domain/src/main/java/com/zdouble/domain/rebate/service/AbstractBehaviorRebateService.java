package com.zdouble.domain.rebate.service;

import com.zdouble.domain.rebate.IBehaviorRebateService;
import com.zdouble.domain.rebate.event.UserBehaviorRebateMessageEvent;
import com.zdouble.domain.rebate.model.aggregate.UserBehaviorRebateAggregate;
import com.zdouble.domain.rebate.model.entity.DailyBehaviorRebateEntity;
import com.zdouble.domain.rebate.model.entity.UserBehaviorEntity;
import com.zdouble.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.zdouble.domain.rebate.repository.IBehaviorRebateRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractBehaviorRebateService implements IBehaviorRebateService {

    protected IBehaviorRebateRepository behaviorRebateRepository;
    protected UserBehaviorRebateMessageEvent userBehaviorRebateMessageEvent;

    public AbstractBehaviorRebateService(IBehaviorRebateRepository behaviorRebateRepository, UserBehaviorRebateMessageEvent userBehaviorRebateMessageEvent) {
        this.behaviorRebateRepository = behaviorRebateRepository;
        this.userBehaviorRebateMessageEvent = userBehaviorRebateMessageEvent;
    }

    @Override
    public List<String> createOrder(UserBehaviorEntity userBehavior) {
        // 1. 查询数据库，获取用户返利行为实体
        List<DailyBehaviorRebateEntity> dailyBehaviorRebates = behaviorRebateRepository.queryDailyBehaviorRebateByBehaviorType(userBehavior.getBehaviorType().getCode());
        // 2. 创建订单对象
        List<UserBehaviorRebateOrderEntity> userBehaviorRebateOrders = createUserBehaviorRebateOrder(userBehavior.getUserId(), dailyBehaviorRebates);
        // 3. 订单聚合对象
        List<UserBehaviorRebateAggregate> userBehaviorRebateAggregates = createUserBehaviorRebateAggregate(userBehavior.getUserId(), userBehaviorRebateOrders);
        // 4. 落库
        behaviorRebateRepository.insertUserBehaviorRebateAggregates(userBehavior.getUserId(), userBehaviorRebateAggregates);
        // 5. 返回订单id
        return userBehaviorRebateOrders.stream().map(UserBehaviorRebateOrderEntity::getOrderId).collect(Collectors.toList());
    }

    protected abstract List<UserBehaviorRebateAggregate> createUserBehaviorRebateAggregate(String userId, List<UserBehaviorRebateOrderEntity> userBehaviorRebateOrders);

    protected abstract List<UserBehaviorRebateOrderEntity> createUserBehaviorRebateOrder(String userId, List<DailyBehaviorRebateEntity> dailyBehaviorRebates);

}
