package com.zdouble.domain.rebate.repository;

import com.zdouble.domain.rebate.model.aggregate.UserBehaviorRebateAggregate;
import com.zdouble.domain.rebate.model.entity.DailyBehaviorRebateEntity;

import java.util.List;

public interface IBehaviorRebateRepository {
    List<DailyBehaviorRebateEntity> queryDailyBehaviorRebateByBehaviorType(String behaviorType);
    void insertUserBehaviorRebateAggregates(String userId, List<UserBehaviorRebateAggregate> userBehaviorRebateAggregates);
}
