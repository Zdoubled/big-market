package com.zdouble.test.domain.award;

import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;
import com.zdouble.domain.award.model.vo.AwardStateVO;
import com.zdouble.domain.award.service.AwardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class AwardServiceTest {

    @Resource
    private AwardService awardService;

    @Resource
    private ThreadPoolExecutor executor;

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                        .awardId(101)
                        .userId("xiaofuge")
                        .activityId(100006L)
                        .strategyId(100301L)
                        .awardState(AwardStateVO.create)
                        .awardTime(new Date())
                        .awardTitle("OpenAI 增加使用次数")
                        .orderId(RandomStringUtils.randomAlphanumeric(12))
                        .build();
                awardService.saveUserAwardRecord(userAwardRecordEntity);
            });
        }
        new CountDownLatch(1).await();
    }
}
