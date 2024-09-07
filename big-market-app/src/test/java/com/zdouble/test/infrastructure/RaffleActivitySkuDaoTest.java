package com.zdouble.test.infrastructure;

import com.alibaba.fastjson.JSON;
import com.zdouble.infrastructure.persistent.dao.RaffleActivitySkuDao;
import com.zdouble.infrastructure.persistent.po.RaffleActivitySku;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleActivitySkuDaoTest {

    @Resource
    private RaffleActivitySkuDao raffleActivitySkuDao;

    @Test
    public void test_query(){
        RaffleActivitySku raffleActivitySku = new RaffleActivitySku();
        raffleActivitySku.setSku(9011L);
        raffleActivitySku.setActivityId(100301L);

        raffleActivitySku = raffleActivitySkuDao.conditionQueryRaffleActivitySku(raffleActivitySku);

        log.info("raffleActivitySku = {}", JSON.toJSONString(raffleActivitySku));
    }

}
