package com.zdouble.domain.activity.service.rule.impl;

import com.zdouble.domain.activity.model.entity.ActivityCountEntity;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.model.pojo.ActivityStateVO;
import com.zdouble.domain.activity.service.rule.AbstractActionChain;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("activity_base_action")
public class ActivityBaseAction extends AbstractActionChain {
    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        // 1. 判断活动状态
        String state = activityEntity.getState();
        if (state.equals(ActivityStateVO.close.getCode())){
            return false;
        }
        // 2. 判断活动时间
        Date beginDateTime = activityEntity.getBeginDateTime();
        Date endDateTime = activityEntity.getEndDateTime();
        Date today = new Date();
        if (today.before(beginDateTime) || today.after(endDateTime)){
            return false;
        }
        // 3. 判断活动库存
        return true;
    }
}
