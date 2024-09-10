package com.zdouble.domain.activity.service.quota.rule.impl;

import com.zdouble.domain.activity.model.entity.ActivityCountEntity;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.model.pojo.ActivityStateVO;
import com.zdouble.domain.activity.service.quota.rule.AbstractActionChain;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("activity_base_action")
@Slf4j
public class ActivityBaseAction extends AbstractActionChain {
    @Override
    public void action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-基础信息【有效期、状态、库存(sku)】校验开始。sku:{} activityId:{}", activitySkuEntity.getSku(), activityEntity.getActivityId());
        // 1. 判断活动状态
        String state = activityEntity.getState();
        if (activityEntity.getState().equals(ActivityStateVO.close.getCode())){
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        // 2. 判断活动时间
        Date beginDateTime = activityEntity.getBeginDateTime();
        Date endDateTime = activityEntity.getEndDateTime();
        Date today = new Date();
        if (today.before(beginDateTime) || today.after(endDateTime)){
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }
        // 3. 判断活动库存
        Integer stockCountSurplus = activitySkuEntity.getStockCountSurplus();
        if (null == stockCountSurplus || stockCountSurplus == 0){
            throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
        }
        next().action(activitySkuEntity, activityEntity, activityCountEntity);
    }
}
