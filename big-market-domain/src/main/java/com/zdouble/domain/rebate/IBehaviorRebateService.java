package com.zdouble.domain.rebate;

import com.zdouble.domain.rebate.model.entity.UserBehaviorEntity;
import com.zdouble.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;

import java.util.List;

public interface IBehaviorRebateService {

    List<String> createOrder(UserBehaviorEntity userBehavior);

    List<UserBehaviorRebateOrderEntity> isCalendarSignRebate(String userId, String outBusinessNo);
}
