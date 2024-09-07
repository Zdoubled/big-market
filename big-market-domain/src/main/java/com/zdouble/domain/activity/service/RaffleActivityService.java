package com.zdouble.domain.activity.service;

import com.zdouble.domain.activity.model.aggregate.CreateOrderAggregate;
import com.zdouble.domain.activity.model.entity.*;
import com.zdouble.domain.activity.model.pojo.OrderStateVO;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.activity.service.rule.factory.DefaultActionChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class RaffleActivityService extends AbstractRaffleActivity{
    public RaffleActivityService(IActivityRepository activityRepository, DefaultActionChainFactory defaultActionChainFactory) {
        super(activityRepository, defaultActionChainFactory);
    }

    @Override
    protected CreateOrderAggregate buildOrderAggregate(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity, ActivitySkuChargeEntity activitySkuChargeEntity) {
        ActivityOrderEntity activityOrderEntity = ActivityOrderEntity.builder()
                .sku(activitySkuEntity.getSku())
                .userId(activitySkuChargeEntity.getUserId())
                .orderId(RandomStringUtils.randomNumeric(12))
                .dayCount(activityCountEntity.getDayCount())
                .monthCount(activityCountEntity.getMonthCount())
                .totalCount(activityCountEntity.getTotalCount())
                .activityId(activityEntity.getActivityId())
                .activityName(activityEntity.getActivityName())
                .strategyId(activityEntity.getStrategyId())
                .orderTime(new Date())
                .state(OrderStateVO.completed)
                .outBusinessNo(activitySkuChargeEntity.getOutBusinessNo())
                .build();

        return CreateOrderAggregate.builder()
                .dayCount(activityCountEntity.getDayCount())
                .dayCountSurplus(activityCountEntity.getDayCount())
                .monthCount(activityCountEntity.getMonthCount())
                .monthCountSurplus(activityCountEntity.getMonthCount())
                .totalCount(activityCountEntity.getTotalCount())
                .totalCountSurplus(activityCountEntity.getTotalCount())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }

    @Override
    protected void doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        activityRepository.saveOrderAggregate(createOrderAggregate);
    }


}
