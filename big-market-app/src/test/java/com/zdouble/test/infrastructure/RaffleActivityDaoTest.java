package com.zdouble.test.infrastructure;

import com.zdouble.infrastructure.persistent.dao.RaffleActivityDao;
import com.zdouble.infrastructure.persistent.po.RaffleActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityDaoTest {
    @Resource
    private RaffleActivityDao raffleActivityDao;

    @Test
    public void test_query() {
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(100301L);
        log.info(raffleActivity.toString());
    }
}

