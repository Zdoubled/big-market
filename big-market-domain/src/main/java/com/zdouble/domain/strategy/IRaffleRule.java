package com.zdouble.domain.strategy;

import java.util.HashMap;

public interface IRaffleRule {
    HashMap<String, Integer> queryRuleLockCount(String[] treeIds);
}
