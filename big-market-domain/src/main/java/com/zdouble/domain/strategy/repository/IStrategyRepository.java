package com.zdouble.domain.strategy.repository;

import com.zdouble.domain.strategy.model.entity.StrategyAwardEntry;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface IStrategyRepository {
    List<StrategyAwardEntry> queryStrategyAwardList(Long strategyId);

    void setStrategyAwardSearchRateTable(Long strategyId, int rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRateRange(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, int rateKey);
}
