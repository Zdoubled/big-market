package com.zdouble.domain.activity.service.rule.impl;

import com.zdouble.domain.activity.model.entity.ActivityCountEntity;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.service.rule.AbstractActionChain;
import org.springframework.stereotype.Component;

@Component("activity_sku_stock_action")
public class ActivitySkuStockAction extends AbstractActionChain {
    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        // 1. 参数校验
        Integer stockCountSurplus = activitySkuEntity.getStockCountSurplus();
        if (null == stockCountSurplus || stockCountSurplus == 0){
            return false;
        }
        return next().action(activitySkuEntity, activityEntity, activityCountEntity);
    }
}
