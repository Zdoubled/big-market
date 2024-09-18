package com.zdouble.domain.activity.service;

import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;

import java.util.List;

public interface IRaffleActivitySkuStockService {
    /**
     * 获取队列值
     * @return
     * @throws InterruptedException
     */
    ActivitySkuStockVO takeQueueValue(Long sku) throws InterruptedException;

    /**
     * 更新sku库存信息
     * @param sku
     * @param activityId
     */
    void updateSkuStock(Long sku, Long activityId);

    /**
     * 清空延迟队列消息
     */
    void clearQueueValue(Long sku);

    /**
     * 库存置零
     */
    void updateSkuStockZero(Long sku);

    /**
     * 获取所有sku
     */
    List<ActivitySkuEntity> queryActivitySkuList();
}
