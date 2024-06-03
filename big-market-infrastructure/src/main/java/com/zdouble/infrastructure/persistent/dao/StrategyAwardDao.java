package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.Award;
import com.zdouble.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StrategyAwardDao {
    public List<StrategyAward> queryStrategyAwardList();

    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);
}
