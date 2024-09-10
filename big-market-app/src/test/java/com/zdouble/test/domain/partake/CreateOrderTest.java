package com.zdouble.test.domain.partake;

import com.zdouble.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.zdouble.domain.activity.model.entity.UserRaffleOrderEntity;
import com.zdouble.domain.activity.service.IRaffleActivityPartakeService;
import com.zdouble.domain.activity.service.armory.IActivityArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class CreateOrderTest {

    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;

    @Test
    public void test_create_order() {
        PartakeRaffleActivityEntity partakeRaffleActivityEntity = new PartakeRaffleActivityEntity();
        partakeRaffleActivityEntity.setUserId("xiaofuge");
        partakeRaffleActivityEntity.setActivityId(100301L);
        UserRaffleOrderEntity order = raffleActivityPartakeService.createOrder(partakeRaffleActivityEntity);
        log.info("下单参与抽奖活动订单 :{}", order);
    }
}
