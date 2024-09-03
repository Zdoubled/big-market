package com.zdouble.domain.strategy.service.rule.tree.factory.engine;

import com.zdouble.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.zdouble.domain.strategy.model.vo.RuleTreeNodeLineVO;
import com.zdouble.domain.strategy.model.vo.RuleTreeNodeVO;
import com.zdouble.domain.strategy.model.vo.RuleTreeVO;
import com.zdouble.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final RuleTreeVO ruleTreeVO;
    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    public DecisionTreeEngine(RuleTreeVO ruleTreeVO, Map<String, ILogicTreeNode> logicTreeNodeGroup){
        this.ruleTreeVO = ruleTreeVO;
        this.logicTreeNodeGroup = logicTreeNodeGroup;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO process(Long strategyId, String userId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardVO strategyAwardVO = null;

        String nextNode = ruleTreeVO.getRuleTreeRootNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();
        while (nextNode != null){
            RuleTreeNodeVO ruleTreeNodeVO = treeNodeMap.get(nextNode);//获取规则树节点
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNodeVO.getRuleKey());//获取节点执行器
            DefaultTreeFactory.TreeActionEntity treeActionEntity = logicTreeNode.logic(strategyId, userId, awardId, ruleTreeNodeVO.getRuleValue());//执行规则过滤
            strategyAwardVO = treeActionEntity.getStrategyAwardVO();
            nextNode = getNextNode(treeActionEntity.getRuleLogicCheckTypeVO(), ruleTreeNodeVO.getTreeNodeLinkList());

            log.info("决策树引擎【{}】treeId:{} node:{} code:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNode, treeActionEntity.getRuleLogicCheckTypeVO().getCode());
        }
        return strategyAwardVO;
    }

    private String getNextNode(RuleLogicCheckTypeVO ruleLogicCheckTypeVO, List<RuleTreeNodeLineVO> treeNodeLinkList) {
        if (treeNodeLinkList == null || treeNodeLinkList.isEmpty()) {
            return null;
        }
        if (ruleLogicCheckTypeVO.equals(RuleLogicCheckTypeVO.TAKE_OVER)) return null;
        for (RuleTreeNodeLineVO ruleTreeNodeLineVO : treeNodeLinkList) {
            if (decision(ruleLogicCheckTypeVO.getCode(), ruleTreeNodeLineVO)) {
                return ruleTreeNodeLineVO.getTreeNodeIdTo();
            }
        }
        throw new RuntimeException("决策树异常,没有找到下一个可执行节点");
    }

    private boolean decision(String matterValue, RuleTreeNodeLineVO ruleTreeNodeLineVO) {
        switch (ruleTreeNodeLineVO.getRuleLimitType()){
            case EQUAL:
                return ruleTreeNodeLineVO.getRuleLimitValue().getCode().equals(matterValue);
            case LT:
            case GT:
            case LE:
            case GE:
            default:
                return false;
        }
    }
}
