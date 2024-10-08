package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.DailyBehaviorRebate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DailyBehaviorRebateDao {
    List<DailyBehaviorRebate> queryDailyBehaviorRebateByBehaviorType(String behaviorType);
}
