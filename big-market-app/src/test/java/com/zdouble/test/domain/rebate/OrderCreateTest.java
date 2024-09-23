package com.zdouble.test.domain.rebate;

import com.zdouble.domain.rebate.IBehaviorRebateService;
import com.zdouble.domain.rebate.model.entity.UserBehaviorEntity;
import com.zdouble.domain.rebate.model.vo.BehaviorTypeVO;
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
public class OrderCreateTest {
    @Resource
    private IBehaviorRebateService behaviorRebateService;

    @Test
    public void test_createOrder(){
        List<String> order = behaviorRebateService.createOrder(UserBehaviorEntity.builder()
                .userId("xiaofuge")
                .behaviorType(BehaviorTypeVO.sign)
                .build()
        );
        order.forEach(log::info);
    }
}
