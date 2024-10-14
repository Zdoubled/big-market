package com.zdouble.test.domain.credit;

import com.zdouble.domain.activity.model.entity.ActivitySkuChargeEntity;
import com.zdouble.domain.activity.model.pojo.OrderTradeTypeVO;
import com.zdouble.domain.activity.service.armory.IActivityArmory;
import com.zdouble.domain.activity.service.quota.RaffleActivityAccountQuotaService;
import com.zdouble.domain.credit.model.entity.TradeEntity;
import com.zdouble.domain.credit.model.vo.TradeNameVO;
import com.zdouble.domain.credit.model.vo.TradeTypeVO;
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
    @Resource
    private RaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @Test
    public void test_credit_pay_trade() {
        ActivitySkuChargeEntity skuRechargeEntity = new ActivitySkuChargeEntity();
        skuRechargeEntity.setUserId("xiaofuge");
        skuRechargeEntity.setSku(9011L);
        // outBusinessNo 作为幂等仿重使用，同一个业务单号2次使用会抛出索引冲突 Duplicate entry '700091009111' for key 'uq_out_business_no' 确保唯一性。
        skuRechargeEntity.setOutBusinessNo("70009240608011");
        skuRechargeEntity.setOrderTradeType(OrderTradeTypeVO.CreditPayPolicy);
        String orderId = raffleActivityAccountQuotaService.createSkuRechargeOrder(skuRechargeEntity);
        log.info("测试结果：{}", orderId);
    }

    @Test
    public void test_credit_recharge() {
        TradeEntity tradeEntity = TradeEntity.builder()
                .userId("xiaofuge")
                .outBusinessNo("70009240608010")
                .tradeAmount(new BigDecimal("-1.68"))
                .tradeName(TradeNameVO.Adjust)
                .tradeType(TradeTypeVO.reverse)
                .build();
        String orderId = creditService.createCreditAdjustOrder(tradeEntity);
        log.info("测试结果：{}", orderId);
    }
}
