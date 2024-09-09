package com.zdouble.domain.activity.service.rule;

import com.zdouble.domain.activity.model.entity.ActivityCountEntity;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;

public interface IActionChain extends IActionChainArmory {
    void action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);
}
