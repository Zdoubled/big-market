package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RuleTreeNodeLine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RuleTreeNodeLineDao {
    List<RuleTreeNodeLine> queryRuleTreeNodeLineByTreeId(String treeId);
}
