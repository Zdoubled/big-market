package com.zdouble.domain.activity.service;

import com.zdouble.domain.activity.model.entity.ActivitySkuChargeEntity;

public interface IRaffleOrder {

    /**
     * 创建sku字数充值订单
     * @param activitySkuChargeEntity
     * @return String 返回的订单唯一表示 out_business_no
     */
    String createSkuRechargeOrder(ActivitySkuChargeEntity activitySkuChargeEntity);
}
