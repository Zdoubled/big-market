package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.domain.strategy.model.entity.StrategyRuleEntity;
import com.zdouble.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import com.zdouble.infrastructure.persistent.po.Award;
import com.zdouble.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StrategyRuleDao {
    public List<StrategyRule> queryStrategyRuleList();

    StrategyRule queryStrategyRule(StrategyRule strategyRule);

    String queryStrategyRuleValue(StrategyRule strategyRule);
}
