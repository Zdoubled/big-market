package com.zdouble.domain.activity.service.rule;

public interface IActionChainArmory {
    IActionChain next();

    IActionChain appendNext(IActionChain actionChain);
}
