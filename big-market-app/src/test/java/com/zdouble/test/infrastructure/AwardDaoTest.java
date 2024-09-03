package com.zdouble.test.infrastructure;

import com.zdouble.infrastructure.persistent.dao.AwardDao;
import com.zdouble.infrastructure.persistent.po.Award;
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
public class AwardDaoTest {

    @Autowired
    private AwardDao awardDao;

    @Test
    public void test_queryAwardList() {
/*        List<Award> awardList = awardDao.queryAwardList();
        for (Award award : awardList) {
            log.info(award.toString());
        }*/
    }
}
