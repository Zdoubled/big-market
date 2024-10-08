package com.zdouble.domain.strategy.service.rule.tree;

import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

import java.util.Date;

public interface ILogicTreeNode {
    DefaultTreeFactory.TreeActionEntity logic(Long strategyId, String userId, Integer awardId, String ruleValue, Date endTime);
}
