package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.Award;
import com.zdouble.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StrategyRuleDao {
    public List<StrategyRule> queryStrategyRuleList();
}
