package com.zdouble.domain.strategy.service.rule.chain;

import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;

public interface ILogicChain extends ILogicChainArmory {
    DefaultLogicChainFactory.StrategyAwardVO logic(Long strategyId, String userId);
}
