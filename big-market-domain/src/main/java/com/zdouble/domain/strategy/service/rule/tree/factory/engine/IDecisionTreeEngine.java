package com.zdouble.domain.strategy.service.rule.tree.factory.engine;

import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

import java.util.Date;

public interface IDecisionTreeEngine {
    DefaultTreeFactory.StrategyAwardVO process(Long strategyId, String userId, Integer awardId, Date endTime);
}
