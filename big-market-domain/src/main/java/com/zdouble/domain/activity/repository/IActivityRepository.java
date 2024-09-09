package com.zdouble.domain.activity.repository;

import com.zdouble.domain.activity.model.aggregate.CreateOrderAggregate;
import com.zdouble.domain.activity.model.entity.ActivityCountEntity;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;

import java.util.Date;

public interface IActivityRepository {
    ActivitySkuEntity queryActivitySku(Long sku);
    
    ActivityEntity queryActivityByActivityId(Long activityId);

    ActivityCountEntity queryActivityCountByActivityCountId(Long activityCountId);

    void saveOrderAggregate(CreateOrderAggregate createOrderAggregate);

    void cacheActivitySkuCount(String cacheKey, Integer stockCont);

    Boolean subtractionSkuStock(String cacheKey, Date endDateTime, Long sku);

    void activitySkuConsumeSendQueue(ActivitySkuStockVO activitySkuStockVO);

    ActivitySkuStockVO takeQueueValue();

    void updateSkuStock(Long sku, Long activityId);

    void clearQueueValue();

    void updateSkuStockZero(Long sku);
}
