package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RuleTreeDao {
    RuleTree queryRuleTreeByTreeId(String treeId);
}
