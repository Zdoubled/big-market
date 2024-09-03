package com.zdouble.domain.strategy.repository;

import com.zdouble.domain.strategy.model.entity.StrategyAwardEntity;
import com.zdouble.domain.strategy.model.entity.StrategyEntity;
import com.zdouble.domain.strategy.model.entity.StrategyRuleEntity;
import com.zdouble.domain.strategy.model.vo.RuleTreeVO;
import com.zdouble.domain.strategy.model.vo.StrategyAwardKeyStockVO;
import com.zdouble.domain.strategy.model.vo.StrategyAwardRuleModelVO;

import java.util.HashMap;
import java.util.List;

public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void setStrategyAwardSearchRateTable(String key, int rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRateRange(Long strategyId);

    int getRateRange(String key);

    Integer getStrategyAwardAssemble(String key, int rateKey);

    StrategyEntity queryStrategyByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, Integer awardId,String ruleModel);

    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    RuleTreeVO queryRuleTreeByTreeId(String treeId);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId);

    void cacheStrategyAwardCount(String cacheKey, Integer awardCount);

    Boolean subtractAwardCount(String cacheKey);

    void awardStockConsumeSendQueue(StrategyAwardKeyStockVO strategyAwardKeyStockVO);

    StrategyAwardKeyStockVO takeQueueValue();

    void updateStrategyAwardStock(Long strategyId, Integer awardId);

    StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId);
}
