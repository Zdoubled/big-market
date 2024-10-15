package com.zdouble.test.domain.activity;

import com.zdouble.domain.activity.model.entity.ActivitySkuChargeEntity;
import com.zdouble.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.zdouble.domain.activity.service.armory.IActivityArmory;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ActivitySkuCountSubtractionTest {
    @Resource
    private IActivityArmory activityArmory;
    @Resource
    private IRaffleActivityAccountQuotaService raffleOrder;

    @Before
    public void init() {
        activityArmory.assembleActivitySku(9011L);
    }


    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            try {
                ActivitySkuChargeEntity charge = ActivitySkuChargeEntity.builder()
                        .sku(9011L)
                        .userId("xiaofuge")
                        .outBusinessNo(RandomStringUtils.randomNumeric(12))
                        .build();
                raffleOrder.createSkuRechargeOrder(charge);
            }catch (AppException e){
                log.warn("订单创建异常：{}", e.getMessage());
            }
        }
        new CountDownLatch(1).await();
    }
}
