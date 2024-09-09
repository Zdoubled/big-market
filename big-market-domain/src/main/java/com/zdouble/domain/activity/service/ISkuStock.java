package com.zdouble.domain.activity.service;

import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;

public interface ISkuStock {
    /**
     * 获取队列值
     * @return
     * @throws InterruptedException
     */
    ActivitySkuStockVO takeQueueValue() throws InterruptedException;

    /**
     * 更新sku库存信息
     * @param sku
     * @param activityId
     */
    void updateSkuStock(Long sku, Long activityId);

    /**
     * 清空延迟队列消息
     */
    void clearQueueValue();

    /**
     * 库存置零
     */
    void updateSkuStockZero(Long sku);
}
