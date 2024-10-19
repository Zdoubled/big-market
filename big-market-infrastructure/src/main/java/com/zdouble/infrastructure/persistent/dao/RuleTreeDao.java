package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RuleTreeDao {
    RuleTree queryRuleTreeByTreeId(@Param("treeId") String treeId);
}
