package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RaffleActivityCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RaffleActivityCountDao {
    RaffleActivityCount queryByActivityCountId(@Param("activityCountId") Long activityCountId);
}
