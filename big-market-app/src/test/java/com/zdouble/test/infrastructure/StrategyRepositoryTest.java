package com.zdouble.test.infrastructure;

import com.zdouble.infrastructure.persistent.dao.RuleTreeDao;
import com.zdouble.infrastructure.persistent.dao.RuleTreeNodeDao;
import com.zdouble.infrastructure.persistent.dao.RuleTreeNodeLineDao;
import com.zdouble.infrastructure.persistent.po.RuleTree;
import com.zdouble.infrastructure.persistent.po.RuleTreeNode;
import com.zdouble.infrastructure.persistent.po.RuleTreeNodeLine;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StrategyRepositoryTest {

    @Autowired
    private RuleTreeDao ruleTreeDao;
    @Autowired
    private RuleTreeNodeDao ruleTreeNodeDao;
    @Autowired
    private RuleTreeNodeLineDao ruleTreeNodeLineDao;

    @Test
    public void test_tree_repository() {
        RuleTree treeLock = ruleTreeDao.queryRuleTreeByTreeId("tree_lock");
        List<RuleTreeNode> treeLockNodes = ruleTreeNodeDao.queryRuleTreeNodeByTreeId("tree_lock");
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.queryRuleTreeNodeLineByTreeId("tree_lock");

        log.info("treeLock: {}", treeLock);
        log.info("treeLockNodes: {}", treeLockNodes);
        log.info("ruleTreeNodeLines: {}", ruleTreeNodeLines);
    }
}
