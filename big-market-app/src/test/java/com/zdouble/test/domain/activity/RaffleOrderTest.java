package com.zdouble.test.domain.activity;

import com.zdouble.domain.activity.model.entity.ActivitySkuChargeEntity;
import com.zdouble.domain.activity.service.IRaffleActivityAccountQuotaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleOrderTest {
    @Resource
    private IRaffleActivityAccountQuotaService raffleOrder;

    @Test
    public void test() {
        ActivitySkuChargeEntity activitySkuChargeEntity = new ActivitySkuChargeEntity();
        activitySkuChargeEntity.setSku(9011L);
        activitySkuChargeEntity.setUserId("xiaofuge");
        activitySkuChargeEntity.setOutBusinessNo("700091009112");
        String orderId = raffleOrder.createSkuRechargeOrder(activitySkuChargeEntity);
        log.info("订单id:{}", orderId);
    }

}
