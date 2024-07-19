package com.zdouble.domain.strategy.service.rule.chain.impl;

import com.zdouble.domain.strategy.service.armory.IStrategyDispatch;
import com.zdouble.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyDispatch strategyDispatch;

    @Override
    public DefaultLogicChainFactory.StrategyAwardVO logic(Long strategyId, String userId) {
        log.info("责任链过滤；默认规则 userId:{},strategyId:{}", userId, strategyId);
        return DefaultLogicChainFactory.StrategyAwardVO.builder()
                .awardId(strategyDispatch.getRandomAwardId(strategyId))
                .logicModel(ruleModel())
                .build();
    }

    @Override
    protected String ruleModel() {
        return "default";
    }
}
