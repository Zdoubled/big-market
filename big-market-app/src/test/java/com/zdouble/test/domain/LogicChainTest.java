package com.zdouble.test.domain;

import com.alibaba.fastjson.JSON;
import com.zdouble.domain.strategy.model.entity.RaffleAwardEntity;
import com.zdouble.domain.strategy.model.entity.RaffleFactorEntity;
import com.zdouble.domain.strategy.service.IRaffleStrategy;
import com.zdouble.domain.strategy.service.rule.chain.ILogicChain;
import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import com.zdouble.domain.strategy.service.rule.chain.impl.DefaultLogicChain;
import com.zdouble.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import com.zdouble.domain.strategy.service.rule.filter.impl.RuleLockLogicFilter;
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
    @Resource
    private RuleLockLogicFilter ruleLockLogicFilter;

    @Before
    public void setUp() {

    }

    @Test
    public void test_default(){
        ILogicChain logicChain = defaultLogicChainFactory.openLogicChain(100003L);
        int awardId = logicChain.logic(100003L, "double");
        log.info("测试结果awardId:{}",awardId);
    }

    @Test
    public void test_rule_blacklist(){
        ILogicChain logicChain = defaultLogicChainFactory.openLogicChain(100001L);
        int awardId = logicChain.logic(100001L, "user003");
        log.info("测试结果awardId:{}",awardId);
    }

    @Test
    public void test_rule_weight(){
        ReflectionTestUtils.setField(ruleWeightLogicChain,"userScore",5000L);
        ILogicChain logicChain = defaultLogicChainFactory.openLogicChain(100001L);
        int awardId = logicChain.logic(100001L, "double");
        log.info("测试结果awardId:{}",awardId);
    }

    @Test
    public void test_performRaffle() {
        ReflectionTestUtils.setField(ruleLockLogicFilter, "userRaffleTimes", 10L);
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("xiaofuge")
                .strategyId(100003L)
                .build();
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }
}
