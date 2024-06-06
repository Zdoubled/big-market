package com.zdouble.test.domain;

import com.zdouble.domain.strategy.service.armory.IStrategyArmory;
import com.zdouble.domain.strategy.service.armory.IStrategyDispatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StrategyArmoryTest {

    @Autowired
    private IStrategyArmory strategyArmory;
    @Autowired
    private IStrategyDispatch strategyDispatch;

    @Test
    public void assembleLotteryStrategy_test(){
        strategyArmory.assembleLotteryStrategy(100001L);
    }

    @Test
    public void getRandomAwardId_test(){
        for (int i = 0; i < 100; i++) {
            log.info("4000:{}",strategyDispatch.getRandomAwardId(100001L,"4000:102,103,104,105"));
            log.info("5000:{}",strategyDispatch.getRandomAwardId(100001L,"5000:102,103,104,105,106,107"));
            log.info("6000:{}",strategyDispatch.getRandomAwardId(100001L,"6000:102,103,104,105,106,107,108,109"));
        }
    }
}
