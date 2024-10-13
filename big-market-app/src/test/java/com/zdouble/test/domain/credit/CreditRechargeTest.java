package com.zdouble.test.domain.credit;

import com.zdouble.domain.credit.model.entity.UserCreditRechargeEntity;
import com.zdouble.domain.credit.service.ICreditService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class CreditRechargeTest {

    @Resource
    private ICreditService creditService;

    @Test
    public void createCreditRechargeOrder() {
        UserCreditRechargeEntity userCreditRechargeEntity = UserCreditRechargeEntity.builder()
                .userId("xiaofuge")
                .creditRecharge(BigDecimal.valueOf(1000L))
                .outBusinessNo("xiaofuge_integral_20240601008")
                .build();
        creditService.createCreditRechargeOrder(userCreditRechargeEntity);

    }

    @Test
    public void createCreditReverseOrder() {
        UserCreditRechargeEntity userCreditRechargeEntity = UserCreditRechargeEntity.builder()
                .userId("xiaofuge")
                .creditRecharge(BigDecimal.valueOf(-1000L))
                .outBusinessNo("xiaofuge_integral_20240601010")
                .build();
        creditService.createCreditRechargeOrder(userCreditRechargeEntity);

    }

}
