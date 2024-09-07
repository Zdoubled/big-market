package com.zdouble.test.domain.activity;

import com.zdouble.domain.activity.model.entity.ActivityOrderEntity;
import com.zdouble.domain.activity.model.entity.ActivitySkuChargeEntity;
import com.zdouble.domain.activity.service.IRaffleOrder;
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
    private IRaffleOrder raffleOrder;

    @Test
    public void test() {
        ActivitySkuChargeEntity shopCardEntity = ActivitySkuChargeEntity.builder()
                .sku(9011L)
                .userId("doublez")
                .build();
        ActivityOrderEntity raffleActivityOrder = raffleOrder.createRaffleActivityOrder(shopCardEntity);

        log.info("raffleActivityOrder:{}", raffleActivityOrder.toString());
    }

}
