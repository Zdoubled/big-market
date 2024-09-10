package com.zdouble.domain.activity.service.quota.rule;

public interface IActionChainArmory {
    IActionChain next();

    IActionChain appendNext(IActionChain actionChain);
}
