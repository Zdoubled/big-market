package com.zdouble.infrastructure.persistent.repository;

import com.zdouble.domain.strategy.model.entity.StrategyAwardEntity;
import com.zdouble.domain.strategy.model.entity.StrategyEntity;
import com.zdouble.domain.strategy.model.entity.StrategyRuleEntity;
import com.zdouble.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.infrastructure.persistent.dao.StrategyAwardDao;
import com.zdouble.infrastructure.persistent.dao.StrategyDao;
import com.zdouble.infrastructure.persistent.dao.StrategyRuleDao;
import com.zdouble.infrastructure.persistent.po.Strategy;
import com.zdouble.infrastructure.persistent.po.StrategyAward;
import com.zdouble.infrastructure.persistent.po.StrategyRule;
import com.zdouble.infrastructure.persistent.redis.IRedisService;
import com.zdouble.types.common.Constants;
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
}
