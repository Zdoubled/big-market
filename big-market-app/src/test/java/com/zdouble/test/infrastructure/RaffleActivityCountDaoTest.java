package com.zdouble.test.infrastructure;

import com.zdouble.infrastructure.persistent.dao.RaffleActivityCountDao;
import com.zdouble.infrastructure.persistent.po.RaffleActivityCount;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleActivityCountDaoTest {
    @Resource
    private RaffleActivityCountDao raffleActivityCountDao;

    @Test
    public void test_queryByActivityCountId() {
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryByActivityCountId(11101L);
        log.info(raffleActivityCount.toString());
    }
}
