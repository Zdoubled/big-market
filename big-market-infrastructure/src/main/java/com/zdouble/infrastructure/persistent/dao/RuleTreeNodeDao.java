package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RuleTreeNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RuleTreeNodeDao {
    List<RuleTreeNode> queryRuleTreeNodeByTreeId(@Param("treeId") String treeId);

    List<RuleTreeNode> queryRuleLockCount(@Param("treeIds") String[] treeIds);
}
