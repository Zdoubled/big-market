package com.zdouble.domain.activity.repository;

import com.zdouble.domain.activity.model.aggregate.CreateOrderAggregate;
import com.zdouble.domain.activity.model.entity.ActivityCountEntity;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;

public interface IActivityRepository {
    ActivitySkuEntity queryActivitySku(Long sku);
    
    ActivityEntity queryActivityByActivityId(Long activityId);

    ActivityCountEntity queryArticleCountByActivityCountId(Long activityCountId);

    void saveOrderAggregate(CreateOrderAggregate createOrderAggregate);
}
