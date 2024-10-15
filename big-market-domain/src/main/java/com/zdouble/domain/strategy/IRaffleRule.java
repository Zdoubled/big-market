package com.zdouble.domain.strategy;

import com.zdouble.domain.strategy.model.vo.RuleWeightVO;

import java.util.HashMap;
import java.util.List;

public interface IRaffleRule {
    /**
     * 奖品规则锁次数查询
     * @param treeIds
     * @return
     */
    HashMap<String, Integer> queryRuleLockCount(String[] treeIds);

    /**
     * 查询奖品权重配置
     * @param articleId
     * @return
     */
    List<RuleWeightVO> queryAwardRuleWeightByArticleId(Long articleId);

}
