package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RuleTreeNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RuleTreeNodeDao {
    List<RuleTreeNode> queryRuleTreeNodeByTreeId(String treeId);

    List<RuleTreeNode> queryRuleLockCount(String[] treeIds);
}
