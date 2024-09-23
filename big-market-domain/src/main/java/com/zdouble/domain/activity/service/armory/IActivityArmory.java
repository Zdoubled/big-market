package com.zdouble.domain.activity.service.armory;

import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;

public interface IActivityArmory {
    /**
     * 根据活动id装配活动sku详细信息
     * @param activityId
     * @return
     */
    Boolean assembleActivitySkuByActivityId(Long activityId);

    /**
     * 根据sku装配活动sku详细信息
     * @param sku
     * @return
     */
    Boolean assembleActivitySku(Long sku);
}
