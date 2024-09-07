package com.zdouble.test.domain.strategy;

import com.alibaba.fastjson.JSON;
import com.zdouble.domain.strategy.model.vo.*;
import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.zdouble.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RuleTreeTest {

    @Resource
    private DefaultTreeFactory defaultTreeFactory;

    @Test
    public void test_tree_rule(){
        RuleTreeNodeVO ruleLock = RuleTreeNodeVO.builder()
                .treeId("rule_lock")
                .ruleKey("rule_lock")
                .ruleValue("1")
                .ruleDesc("限定用户完成n次抽奖后解锁")
                .treeNodeLinkList(
                        new ArrayList<RuleTreeNodeLineVO>(){
                            {
                                add(RuleTreeNodeLineVO.builder()
                                        .treeNodeIdFrom("rule_lock")
                                        .treeNodeIdTo("rule_luck_award")
                                        .ruleLimitType(RuleLimitTypeVO.EQUAL)
                                        .ruleLimitValue(RuleLogicCheckTypeVO.TAKE_OVER)
                                        .build());

                                add(RuleTreeNodeLineVO.builder()
                                        .treeNodeIdFrom("rule_lock")
                                        .treeNodeIdTo("rule_stock")
                                        .ruleLimitType(RuleLimitTypeVO.EQUAL)
                                        .ruleLimitValue(RuleLogicCheckTypeVO.ALLOW)
                                        .build());
                            }
                        }
                )
                .build();

        RuleTreeNodeVO ruleLuckAward = RuleTreeNodeVO.builder()
                .treeId("rule_lock")
                .ruleKey("rule_luck_award")
                .ruleValue("1")
                .ruleDesc("兜底奖品")
                .treeNodeLinkList(null)
                .build();

        RuleTreeNodeVO ruleStock = RuleTreeNodeVO.builder()
                .treeId("rule_lock")
                .ruleKey("rule_stock")
                .ruleValue(null)
                .ruleDesc("扣减库存节点")
                .treeNodeLinkList(
                        new ArrayList<RuleTreeNodeLineVO>(){
                            {
                                add(RuleTreeNodeLineVO.builder()
                                        .treeNodeIdFrom("rule_stock")
                                        .treeNodeIdTo("rule_luck_award")
                                        .ruleLimitType(RuleLimitTypeVO.EQUAL)
                                        .ruleLimitValue(RuleLogicCheckTypeVO.TAKE_OVER)
                                        .build());
                            }
                        }
                )
                .build();

        RuleTreeVO ruleTreeVO = new RuleTreeVO();
        ruleTreeVO.setTreeId("rule_lock");
        ruleTreeVO.setTreeName("决策树规则；增加dall-e-3画图模型");
        ruleTreeVO.setTreeDesc("决策树规则；增加dall-e-3画图模型");
        ruleTreeVO.setRuleTreeRootNode("rule_lock");

        ruleTreeVO.setTreeNodeMap(new HashMap<String, RuleTreeNodeVO>() {{
            put("rule_lock", ruleLock);
            put("rule_stock", ruleStock);
            put("rule_luck_award", ruleLuckAward);
        }});

        IDecisionTreeEngine decisionTreeEngine = defaultTreeFactory.openTreeEngine(ruleTreeVO);
        DefaultTreeFactory.StrategyAwardVO result = decisionTreeEngine.process(100001L, "double", 100);

        log.info("测试结果: " + JSON.toJSONString(result));
    }
}
