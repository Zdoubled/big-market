package com.zdouble.domain.rebate.service;

import com.zdouble.domain.rebate.IBehaviorRebateService;
import com.zdouble.domain.rebate.event.UserBehaviorRebateMessageEvent;
import com.zdouble.domain.rebate.model.aggregate.UserBehaviorRebateAggregate;
import com.zdouble.domain.rebate.model.entity.DailyBehaviorRebateEntity;
import com.zdouble.domain.rebate.model.entity.UserBehaviorEntity;
import com.zdouble.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.zdouble.domain.rebate.repository.IBehaviorRebateRepository;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
        // TODO 此处得进行过滤, 如 : 用户今天是否获得登录行为返利
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
