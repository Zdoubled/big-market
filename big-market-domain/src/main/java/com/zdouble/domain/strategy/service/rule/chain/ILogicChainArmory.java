package com.zdouble.domain.strategy.service.rule.chain;

public interface ILogicChainArmory {
    ILogicChain next();

    ILogicChain appendNext(ILogicChain logicChain);
}
