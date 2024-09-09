package com.zdouble.domain.activity.service.rule.impl;

import com.zdouble.domain.activity.model.entity.ActivityCountEntity;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.activity.service.rule.AbstractActionChain;
import com.zdouble.domain.activity.service.rule.armory.IActivityArmory;
import com.zdouble.domain.activity.service.rule.armory.IActivityDispatch;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("activity_sku_stock_action")
public class ActivitySkuStockAction extends AbstractActionChain {

    @Resource
    private IActivityDispatch activityDispatch;
    @Resource
    private IActivityRepository activityRepository;

    @Override
    public void action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        /**
         * 库存扣减规则链，同样采用redis延迟队列实现异步处理
         */
        // 扣减库存
        Boolean status = activityDispatch.subtractionSkuStock(activitySkuEntity.getSku(), activityEntity.getEndDateTime());
        if (status) {
            // 成功后同步到mysql, 加入sku库存扣减任务到延迟队列中
            activityRepository.activitySkuConsumeSendQueue(ActivitySkuStockVO.builder()
                    .activityId(activitySkuEntity.getActivityId())
                    .sku(activitySkuEntity.getSku())
                    .build());
            return;
        }
        throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
    }
}
