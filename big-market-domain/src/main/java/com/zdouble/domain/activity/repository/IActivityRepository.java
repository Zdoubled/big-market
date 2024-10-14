package com.zdouble.domain.activity.repository;

import com.zdouble.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.zdouble.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.zdouble.domain.activity.model.entity.*;
import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;
import com.zdouble.domain.credit.model.entity.DeliveryOrderEntity;

import java.util.Date;
import java.util.List;

public interface IActivityRepository {
    ActivitySkuEntity queryActivitySku(Long sku);
    
    ActivityEntity queryActivityByActivityId(Long activityId);

    ActivityCountEntity queryActivityCountByActivityCountId(Long activityCountId);

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

    Integer queryRaffleActivityTotalPartakeCount(String userId, Long activityId);

    List<ActivitySkuEntity> queryActivitySkuList();

    void doSaveCreditPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    void doSaveNoPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    void updateOrder(DeliveryOrderEntity deliveryOrderEntity);
}
