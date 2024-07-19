package com.zdouble.domain.strategy.service.rule.tree.impl;

import com.zdouble.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.zdouble.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.springframework.stereotype.Component;

@Component("rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(Long strategyId, String userId, Integer awardId) {
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                        .awardId(101)
                        .awardValue("1,100")
                        .build())
                .build();
    }
}
