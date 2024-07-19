package com.zdouble.infrastructure.persistent.repository;

import com.zdouble.domain.strategy.model.entity.StrategyAwardEntity;
import com.zdouble.domain.strategy.model.entity.StrategyEntity;
import com.zdouble.domain.strategy.model.entity.StrategyRuleEntity;
import com.zdouble.domain.strategy.model.vo.*;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.infrastructure.persistent.dao.*;
import com.zdouble.infrastructure.persistent.po.*;
import com.zdouble.infrastructure.persistent.redis.IRedisService;
import com.zdouble.types.common.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class StrategyRepository implements IStrategyRepository {
    @Resource
    private IRedisService redisService;
    @Resource
    private StrategyAwardDao strategyAwardDao;
    @Resource
    private StrategyDao strategyDao;
    @Resource
    private StrategyRuleDao strategyRuleDao;
    @Resource
    private RuleTreeDao ruleTreeDao;
    @Resource
    private RuleTreeNodeDao ruleTreeNodeDao;
    @Resource
    private RuleTreeNodeLineDao ruleTreeNodeLineDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        //1.查询redis缓存
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntryList = redisService.getValue(cacheKey);
        if (null != strategyAwardEntryList && !strategyAwardEntryList.isEmpty()) {
            return strategyAwardEntryList;
        }
        //2.从库中查询策略配置,并缓存到redis中
        List<StrategyAward> strategyAwardList = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntryList = strategyAwardList.stream().map(strategyAward -> {
            return StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
        }).collect(Collectors.toList());
        redisService.setValue(cacheKey, strategyAwardEntryList);
        return strategyAwardEntryList;
    }

    @Override
    public void setStrategyAwardSearchRateTable(String key, int rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        //1.缓存策略的概率范围
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, rateRange);
        //2.缓存策略的抽奖概率范围表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return getRateRange(String.valueOf(strategyId));
    }

    @Override
    public int getRateRange(String key) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, int rateKey) {
        return (Integer) redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key).get(rateKey);
    }

    @Override
    public StrategyEntity queryStrategyByStrategyId(Long strategyId) {
        //1.查询redis缓存
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntry = redisService.getValue(cacheKey);
        if (null != strategyEntry) {
            return strategyEntry;
        }
        //2.从库中查询策略配置,并缓存到redis中
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        StrategyEntity strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .ruleModel(strategy.getRuleModel())
                .strategyDesc(strategy.getStrategyDesc())
                .build();
        redisService.setValue(cacheKey, strategyEntity);

        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setRuleModel(ruleModel);
        strategyRule = strategyRuleDao.queryStrategyRule(strategyRule);
        return StrategyRuleEntity.builder()
                .strategyId(strategyRule.getStrategyId())
                .ruleModel(strategyRule.getRuleModel())
                .ruleDesc(strategyRule.getRuleDesc())
                .ruleValue(strategyRule.getRuleValue())
                .ruleType(strategyRule.getRuleType())
                .awardId(strategyRule.getAwardId())
                .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId,String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setAwardId(awardId);
        strategyRule.setRuleModel(ruleModel);
        return strategyRuleDao.queryStrategyRuleValue(strategyRule);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return queryStrategyRuleValue(strategyId, null,ruleModel);
    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModel(Long strategyId, Integer awardId) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setAwardId(awardId);
        return StrategyAwardRuleModelVO.builder().ruleModels(strategyAwardDao.queryStrategyAwardRuleModel(strategyRule)).build();
    }

    @Override
    public RuleTreeVO queryRuleTreeByTreeId(String treeId) {
        //1.查询redis缓存
        String cacheKey = Constants.RedisKey.RULE_TREE_KEY + treeId;
        RuleTreeVO ruleTreeVO = redisService.getValue(cacheKey);
        if (null != ruleTreeVO) {
            return ruleTreeVO;
        }

        //2.从库中查询策略配置,并缓存到redis中
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.queryRuleTreeNodeLineByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleTreeNodeByTreeId(treeId);
        //类型转换
        List<RuleTreeNodeVO> ruleTreeNodeVOS = ruleTreeNodes.stream().map(ruleTreeNode -> {
            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOS = ruleTreeNodeLines.stream()
                    .map(ruleTreeNodeLine -> {
                        return RuleTreeNodeLineVO.builder()
                                .treeId(treeId)
                                .treeNodeIdFrom(ruleTreeNodeLine.getRuleNodeFrom())
                                .treeNodeIdTo(ruleTreeNodeLine.getRuleNodeTo())
                                .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                                .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                                .build();
                    })
                    .filter(ruleTreeNodeLineVO -> ruleTreeNodeLineVO.getTreeNodeIdFrom().equals(ruleTreeNode.getRuleKey()))
                    .collect(Collectors.toList());

            return RuleTreeNodeVO.builder()
                    .treeId(treeId)
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .treeNodeLinkList(ruleTreeNodeLineVOS)
                    .build();
        }).collect(Collectors.toList());

        HashMap<String, RuleTreeNodeVO> ruleTreeNodeMap = new HashMap<>();
        for (RuleTreeNodeVO ruleTreeNodeVO : ruleTreeNodeVOS) {
            String ruleKey = ruleTreeNodeVO.getRuleKey();
            ruleTreeNodeMap.put(ruleKey, ruleTreeNodeVO);
        }

        RuleTree ruleTree = ruleTreeDao.queryRuleTreeByTreeId(treeId);

        ruleTreeVO = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .ruleTreeRootNode(ruleTree.getTreeNodeRuleKey())
                .treeDesc(ruleTree.getTreeDesc())
                .treeName(ruleTree.getTreeName())
                .treeNodeMap(ruleTreeNodeMap)
                .build();
        redisService.setValue(cacheKey, ruleTreeVO);
        return ruleTreeVO;
    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId) {
        return strategyRuleDao.queryStrategyAwardRule(strategyId, awardId);
    }
}
