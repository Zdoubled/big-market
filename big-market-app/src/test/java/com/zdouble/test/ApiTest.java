package com.zdouble.test;

import com.alibaba.fastjson.JSON;
import com.zdouble.IRaffleActivityService;
import com.zdouble.domain.activity.service.armory.IActivityArmory;
import com.zdouble.dto.ActivityDrawRequestDto;
import com.zdouble.dto.ActivityDrawResponseDto;
import com.zdouble.dto.SkuProductResponseDto;
import com.zdouble.dto.SkuProductShopCartRequestDTO;
import com.zdouble.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private IRaffleActivityService raffleActivityService;

    @Test
    public void test_blacklist_draw() throws InterruptedException {
        ActivityDrawRequestDto request = new ActivityDrawRequestDto();
        request.setActivityId(100301L);
        request.setUserId("user003");
        Response<ActivityDrawResponseDto> response = raffleActivityService.draw(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));

        new CountDownLatch(1).await();
    }

    @Test
    public void test_querySkuProductListByActivityId() {
        Long request = 100301L;
        Response<List<SkuProductResponseDto>> response = raffleActivityService.querySkuProductListByActivityId(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryUserCreditAccount() {
        String request = "xiaofuge";
        Response<BigDecimal> response = raffleActivityService.queryUserCreditAccount(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_creditPayExchangeSku() throws InterruptedException {
        SkuProductShopCartRequestDTO request = new SkuProductShopCartRequestDTO();
        request.setUserId("xiaofuge");
        request.setSku(9011L);
        Response<Boolean> response = raffleActivityService.creditPayExchangeSku(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
        new CountDownLatch(1).await();
    }
}
