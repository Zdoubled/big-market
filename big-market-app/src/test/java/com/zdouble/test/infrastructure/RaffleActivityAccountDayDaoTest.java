package com.zdouble.test.infrastructure;

import com.zdouble.infrastructure.persistent.dao.RaffleActivityAccountDayDao;
import com.zdouble.infrastructure.persistent.po.RaffleActivityAccountDay;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleActivityAccountDayDaoTest {

    @Resource
    private RaffleActivityAccountDayDao raffleActivityAccountDayDao;

    public void test_queryRaffleActivityAccountDayList() {
        List<RaffleActivityAccountDay> raffleActivityAccountDays = raffleActivityAccountDayDao.queryRaffleActivityAccountDayList();
        log.info(raffleActivityAccountDays.toString());
    }

}
