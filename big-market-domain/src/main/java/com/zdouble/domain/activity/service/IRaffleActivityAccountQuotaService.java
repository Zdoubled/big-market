package com.zdouble.domain.activity.service;

import com.zdouble.domain.activity.model.entity.ActivitySkuChargeEntity;

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
    Integer queryRaffleActivityPartakeCount(String userId, Long activityId);
}
