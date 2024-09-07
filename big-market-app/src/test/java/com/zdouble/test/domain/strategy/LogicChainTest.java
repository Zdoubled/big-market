package com.zdouble.test.domain.strategy;

import com.alibaba.fastjson.JSON;
import com.zdouble.domain.strategy.model.entity.RaffleAwardEntity;
import com.zdouble.domain.strategy.model.entity.RaffleFactorEntity;
import com.zdouble.domain.strategy.service.IRaffleStrategy;
import com.zdouble.domain.strategy.service.rule.chain.ILogicChain;
import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import com.zdouble.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LogicChainTest {
    @Resource
    private DefaultLogicChainFactory defaultLogicChainFactory;
    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;
    @Resource
    private IRaffleStrategy raffleStrategy;

    @Before
    public void setUp() {

    }

    @Test
    public void test_default(){
        ILogicChain logicChain = defaultLogicChainFactory.openLogicChain(100003L);
        DefaultLogicChainFactory.StrategyAwardVO strategyAwardVO = logicChain.logic(100003L, "double");
        log.info("测试结果awardId:{}",strategyAwardVO);
    }

    @Test
    public void test_rule_blacklist(){
        ILogicChain logicChain = defaultLogicChainFactory.openLogicChain(100001L);
        DefaultLogicChainFactory.StrategyAwardVO strategyAwardVO = logicChain.logic(100001L, "user003");
        log.info("测试结果awardId:{}",strategyAwardVO);
    }

    @Test
    public void test_rule_weight(){
        ReflectionTestUtils.setField(ruleWeightLogicChain,"userScore",5000L);
        ILogicChain logicChain = defaultLogicChainFactory.openLogicChain(100001L);
        DefaultLogicChainFactory.StrategyAwardVO strategyAwardVO = logicChain.logic(100001L, "double");
        log.info("测试结果awardId:{}",strategyAwardVO);
    }

}
