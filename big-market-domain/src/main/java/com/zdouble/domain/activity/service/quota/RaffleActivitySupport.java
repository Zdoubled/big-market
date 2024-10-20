package com.zdouble.domain.activity.service.quota;

import com.zdouble.domain.activity.model.entity.ActivityCountEntity;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.activity.service.quota.policy.ITradePolicy;
import com.zdouble.domain.activity.service.quota.rule.factory.DefaultActionChainFactory;

import java.util.Map;

public abstract class RaffleActivitySupport {

    protected IActivityRepository activityRepository;

    protected DefaultActionChainFactory defaultActionChainFactory;

    protected Map<String, ITradePolicy> tradePolicyMap;

    public RaffleActivitySupport(IActivityRepository activityRepository, DefaultActionChainFactory defaultActionChainFactory, Map<String, ITradePolicy> tradePolicyMap) {
        this.activityRepository = activityRepository;
        this.defaultActionChainFactory = defaultActionChainFactory;
        this.tradePolicyMap = tradePolicyMap;
    }

    public RaffleActivitySupport(IActivityRepository activityRepository, DefaultActionChainFactory defaultActionChainFactory) {
        this.activityRepository = activityRepository;
        this.defaultActionChainFactory = defaultActionChainFactory;
    }

    public ActivitySkuEntity queryActivitySku(Long sku) {
        return activityRepository.queryActivitySku(sku);
    }

    public ActivityEntity queryActivityByActivityId(Long articleId) {
        return activityRepository.queryActivityByActivityId(articleId);
    }

    public ActivityCountEntity queryActivityCountByActivityCountId(Long activityCountId) {
        return activityRepository.queryActivityCountByActivityCountId(activityCountId);
    }
}