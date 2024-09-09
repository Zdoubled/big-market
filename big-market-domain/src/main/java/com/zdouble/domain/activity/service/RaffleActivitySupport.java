package com.zdouble.domain.activity.service;

import com.zdouble.domain.activity.model.entity.ActivityCountEntity;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.activity.service.rule.factory.DefaultActionChainFactory;

public abstract class RaffleActivitySupport {

    protected IActivityRepository activityRepository;

    protected DefaultActionChainFactory defaultActionChainFactory;

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