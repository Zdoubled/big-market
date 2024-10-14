package com.zdouble.domain.activity.service.quota;

import com.zdouble.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.zdouble.domain.activity.model.entity.*;
import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;
import com.zdouble.domain.activity.model.pojo.OrderStateVO;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.activity.service.IRaffleActivitySkuStockService;
import com.zdouble.domain.activity.service.quota.policy.ITradePolicy;
import com.zdouble.domain.activity.service.quota.rule.factory.DefaultActionChainFactory;
import com.zdouble.domain.credit.model.entity.DeliveryOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RaffleActivityAccountQuotaService extends AbstractRaffleActivityAccountQuota implements IRaffleActivitySkuStockService {


    public RaffleActivityAccountQuotaService(IActivityRepository activityRepository, DefaultActionChainFactory defaultActionChainFactory, Map<String, ITradePolicy> tradePolicyMap) {
        super(activityRepository, defaultActionChainFactory, tradePolicyMap);
    }

    @Override
    protected CreateQuotaOrderAggregate buildOrderAggregate(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity, ActivitySkuChargeEntity activitySkuChargeEntity) {
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
                .payAmount(activitySkuEntity.getProductAmount())
                .state(OrderStateVO.wait_pay)
                .outBusinessNo(activitySkuChargeEntity.getOutBusinessNo())
                .build();

        return CreateQuotaOrderAggregate.builder()
                .userId(activitySkuChargeEntity.getUserId())
                .activityId(activitySkuEntity.getActivityId())
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
    public ActivitySkuStockVO takeQueueValue(Long sku) throws InterruptedException {
        return activityRepository.takeQueueValue(sku);
    }

    @Override
    public void updateSkuStock(Long sku, Long activityId) {
        activityRepository.updateSkuStock(sku, activityId);
    }

    @Override
    public void clearQueueValue(Long sku) {
        activityRepository.clearQueueValue(sku);
    }

    @Override
    public void updateSkuStockZero(Long sku) {
        activityRepository.updateSkuStockZero(sku);
    }

    @Override
    public List<ActivitySkuEntity> queryActivitySkuList() {
        return activityRepository.queryActivitySkuList();
    }

    @Override
    public Integer queryRaffleActivityTotalPartakeCount(String userId, Long activityId) {
        return activityRepository.queryRaffleActivityTotalPartakeCount(userId, activityId);
    }

    @Override
    public ActivityAccountEntity queryActivityAccountQuotaService(ActivityAccountEntity activityAccountEntity) {
        return activityRepository.queryActivityAccount(activityAccountEntity.getUserId(), activityAccountEntity.getActivityId());
    }

    @Override
    public void updateOrder(DeliveryOrderEntity deliveryOrderEntity) {
        activityRepository.updateOrder(deliveryOrderEntity);
    }
}
