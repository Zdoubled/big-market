package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RaffleActivityDao {

    void insert(RaffleActivity raffleActivity);

    RaffleActivity queryRaffleActivityByActivityId(@Param("activityId") Long activityId);

    Long queryStrategyIdByActivityId(@Param("articleId") Long articleId);

    Long queryActivityIdByStrategyId(@Param("strategyId") Long strategyId);
}
