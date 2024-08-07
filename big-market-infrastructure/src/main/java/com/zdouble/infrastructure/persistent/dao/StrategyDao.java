package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.domain.strategy.model.entity.StrategyEntity;
import com.zdouble.infrastructure.persistent.po.Award;
import com.zdouble.infrastructure.persistent.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StrategyDao {
    public List<Strategy> queryStrategyList();

    Strategy queryStrategyByStrategyId(Long strategyId);
}
