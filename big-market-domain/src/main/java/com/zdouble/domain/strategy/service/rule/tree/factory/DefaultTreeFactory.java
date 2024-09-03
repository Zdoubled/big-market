package com.zdouble.domain.strategy.service.rule.tree.factory;

import com.zdouble.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.zdouble.domain.strategy.model.vo.RuleTreeVO;
import com.zdouble.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.zdouble.domain.strategy.service.rule.tree.factory.engine.DecisionTreeEngine;
import com.zdouble.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultTreeFactory {
    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicTreeNodeGroup) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
    }

    /**
     * 将规则树 ruleTreeVO 交给规则引擎,执行规则过滤
     * @param ruleTreeVO
     * @return
     */
    public IDecisionTreeEngine openTreeEngine(RuleTreeVO ruleTreeVO) {
        return new DecisionTreeEngine(ruleTreeVO, logicTreeNodeGroup);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TreeActionEntity{
        private RuleLogicCheckTypeVO ruleLogicCheckTypeVO;
        private StrategyAwardVO strategyAwardVO;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StrategyAwardVO{
        private Integer awardId;
        private String awardValue;
    }
}
