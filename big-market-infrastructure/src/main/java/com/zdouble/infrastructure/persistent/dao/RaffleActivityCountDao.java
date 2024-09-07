package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RaffleActivityCount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RaffleActivityCountDao {
    RaffleActivityCount queryByActivityCountId(Long activityCountId);
}
