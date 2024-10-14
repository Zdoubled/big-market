package com.zdouble.domain.activity.service;

import com.zdouble.domain.activity.model.entity.ActivityAccountEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuChargeEntity;
import com.zdouble.domain.credit.model.entity.DeliveryOrderEntity;

public interface IRaffleActivityAccountQuotaService {

    /**
     * 创建sku字数充值订单
     * @param activitySkuChargeEntity
     * @return String 返回的订单唯一表示 out_business_no
     */
    String createSkuRechargeOrder(ActivitySkuChargeEntity activitySkuChargeEntity);

    /**
     * 查询当天用户参与抽奖次数
     * @param userId
     * @param activityId
     * @return
     */
    Integer queryRaffleActivityTotalPartakeCount(String userId, Long activityId);

    /**
     * 查询用户账户额度信息
     * @param activityAccountEntity
     * @return
     */
    ActivityAccountEntity queryActivityAccountQuotaService(ActivityAccountEntity activityAccountEntity);

    /**
     * 修改订单状态
     * @param deliveryOrderEntity
     */
    void updateOrder(DeliveryOrderEntity deliveryOrderEntity);
}
