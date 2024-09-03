package com.zdouble.test.infrastructure;

import com.alibaba.fastjson.JSON;
import com.zdouble.domain.strategy.model.entity.RaffleAwardEntity;
import com.zdouble.domain.strategy.model.entity.RaffleFactorEntity;
import com.zdouble.domain.strategy.service.AbstractRaffleStrategy;
import com.zdouble.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SubtractAwardCountTest {

    @Resource
    private AbstractRaffleStrategy raffleStrategy;

    @Test
    public void test_subtractAwardCount() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("doubleZ")
                .strategyId(100006L)
                .build();
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
        log.info("抽奖因子: {}", JSON.toJSONString(raffleFactorEntity));
        log.info("抽奖结果: {}", JSON.toJSONString(raffleAwardEntity));
    }
}
