package com.zdouble.domain.strategy.service.rule.tree.factory.engine;

import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

public interface IDecisionTreeEngine {
    DefaultTreeFactory.StrategyAwardVO process(Long strategyId, String userId, Integer awardId);
}
