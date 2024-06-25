package com.zdouble.domain.strategy.service.rule.chain.impl;

import com.zdouble.domain.strategy.service.armory.IStrategyDispatch;
import com.zdouble.domain.strategy.service.rule.chain.AbstractLogicChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyDispatch strategyDispatch;

    @Override
    public Integer logic(Long strategyId, String userId) {
        log.info("责任链过滤；默认规则 userId:{},strategyId:{}", userId, strategyId);
        return strategyDispatch.getRandomAwardId(strategyId);
    }

    @Override
    protected String ruleModel() {
        return "default";
    }
}
