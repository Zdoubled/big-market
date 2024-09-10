package com.zdouble.domain.activity.service.armory;

import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.activity.service.armory.IActivityArmory;
import com.zdouble.domain.activity.service.armory.IActivityDispatch;
import com.zdouble.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 活动信息缓存预热
 */
@Service
@Slf4j
public class ActivityArmory implements IActivityArmory, IActivityDispatch {

    @Resource
    private IActivityRepository activityRepository;


    @Override
    public Boolean assembleActivitySku(Long sku) {
        // 1. 缓存activity_sku
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(sku);
        cacheActivitySkuCount(sku, activitySkuEntity.getStockCount());
        // 2. 缓存activity
        activityRepository.queryActivityByActivityId(activitySkuEntity.getActivityId());
        // 3. 缓存activity_count
        activityRepository.queryActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        return true;
    }

    private void cacheActivitySkuCount(Long sku, Integer stockCont) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_KEY + sku;
        activityRepository.cacheActivitySkuCount(cacheKey, stockCont);
    }

    @Override
    public Boolean subtractionSkuStock(Long sku, Date endDateTime) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_KEY + sku;
        return activityRepository.subtractionSkuStock(cacheKey, endDateTime, sku);
    }
}
