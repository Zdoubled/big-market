package com.zdouble.infrastructure.persistent.repository;

import com.zdouble.domain.strategy.model.entity.StrategyAwardEntry;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.infrastructure.persistent.dao.StrategyAwardDao;
import com.zdouble.infrastructure.persistent.po.StrategyAward;
import com.zdouble.infrastructure.persistent.redis.IRedisService;
import com.zdouble.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    @Override
    public List<StrategyAwardEntry> queryStrategyAwardList(Long strategyId) {
        //1.查询redis缓存
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntry> strategyAwardEntryList = redisService.getValue(cacheKey);
        if (null != strategyAwardEntryList && !strategyAwardEntryList.isEmpty()) {
            return strategyAwardEntryList;
        }
        //2.从库中查询策略配置,并缓存到redis中
        List<StrategyAward> strategyAwardList = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntryList = strategyAwardList.stream().map(strategyAward -> {
            return StrategyAwardEntry.builder()
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
    public void setStrategyAwardSearchRateTable(Long strategyId, int rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        //1.缓存策略的概率范围
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId, rateRange);
        //2.缓存策略的抽奖概率范围表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, int rateKey) {
        return (Integer) redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId).get(rateKey);
    }
}
