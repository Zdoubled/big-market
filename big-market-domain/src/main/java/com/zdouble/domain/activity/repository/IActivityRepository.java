package com.zdouble.domain.activity.repository;

import com.zdouble.domain.activity.model.aggregate.CreateOrderAggregate;
import com.zdouble.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.zdouble.domain.activity.model.entity.*;
import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;

import java.util.Date;
import java.util.List;

public interface IActivityRepository {
    ActivitySkuEntity queryActivitySku(Long sku);
    
    ActivityEntity queryActivityByActivityId(Long activityId);

    ActivityCountEntity queryActivityCountByActivityCountId(Long activityCountId);

    void saveOrderAggregate(CreateOrderAggregate createOrderAggregate);

    void cacheActivitySkuCount(String cacheKey, Integer stockCont);

    Boolean subtractionSkuStock(String cacheKey, Date endDateTime, Long sku);

    void activitySkuConsumeSendQueue(ActivitySkuStockVO activitySkuStockVO);

    ActivitySkuStockVO takeQueueValue(Long sku);

    void updateSkuStock(Long sku, Long activityId);

    void clearQueueValue(Long sku);

    void updateSkuStockZero(Long sku);

    UserRaffleOrderEntity queryUserRaffleOrder(Long activityId, String userId);

    ActivityAccountEntity queryActivityAccount(String userId, Long activityId);

    void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    ActivityAccountMonthEntity queryActivityAccountMonth(String userId, Long activityId, String month);

    ActivityAccountDayEntity queryActivityAccountDay(String userId, Long activityId, String day);

    List<ActivitySkuEntity> queryActivitySkuByActivityId(Long activityId);

    Long queryStrategyIdByActivityId(Long articleId);

    Integer queryRaffleActivityPartakeCount(String userId, Long activityId);

    List<ActivitySkuEntity> queryActivitySkuList();
}
