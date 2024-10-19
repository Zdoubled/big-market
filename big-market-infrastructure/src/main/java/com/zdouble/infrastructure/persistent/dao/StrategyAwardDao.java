package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StrategyAwardDao {
    List<StrategyAward> queryStrategyAwardList();

    List<StrategyAward> queryStrategyAwardListByStrategyId(@Param("strategyId") Long strategyId);

    void updateStrategyAwardStock(StrategyAward strategyAward);

    String queryStrategyAwardRuleModels(StrategyAward strategyAward);

    StrategyAward queryStrategyAward(StrategyAward strategyAward);
}
