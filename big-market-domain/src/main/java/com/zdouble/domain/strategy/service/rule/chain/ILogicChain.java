package com.zdouble.domain.strategy.service.rule.chain;

public interface ILogicChain extends ILogicChainArmory {
    Integer logic(Long strategyId, String userId);
}
