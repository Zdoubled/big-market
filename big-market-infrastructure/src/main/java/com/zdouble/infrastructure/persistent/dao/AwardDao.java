package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.Award;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AwardDao {
    public List<Award> queryAwardList();

    String queryAwardKey(@Param("awardId") Integer awardId);
}
