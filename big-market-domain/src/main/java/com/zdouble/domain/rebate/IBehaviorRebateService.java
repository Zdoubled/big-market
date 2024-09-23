package com.zdouble.domain.rebate;

import com.zdouble.domain.rebate.model.entity.UserBehaviorEntity;

import java.util.List;

public interface IBehaviorRebateService {

    List<String> createOrder(UserBehaviorEntity userBehavior);
}
