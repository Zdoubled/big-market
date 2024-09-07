package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RaffleActivityDao {

    void insert(RaffleActivity raffleActivity);

    RaffleActivity queryRaffleActivityByActivityId(Long activityId);
}
