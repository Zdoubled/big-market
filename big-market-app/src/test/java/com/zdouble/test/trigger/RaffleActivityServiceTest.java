package com.zdouble.test.trigger;

import com.alibaba.fastjson.JSON;
import com.zdouble.IRaffleActivityService;
import com.zdouble.dto.ActivityAccountQuotaRequestDto;
import com.zdouble.dto.ActivityAccountQuotaResponseDto;
import com.zdouble.dto.RaffleStrategyRuleWeightRequestDto;
import com.zdouble.dto.RaffleStrategyRuleWeightResponseDto;
import com.zdouble.types.model.Response;
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
public class RaffleActivityServiceTest {

    @Resource
    private IRaffleActivityService raffleActivityService;

    @Test
    public void test_isCalendarSignRebate() {
        Response<Boolean> response = raffleActivityService.isCalendarSignRebate("xiaofuge");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryUserActivityAccount() {
        ActivityAccountQuotaRequestDto request = new ActivityAccountQuotaRequestDto();
        request.setActivityId(100301L);
        request.setUserId("xiaofuge");
        // 查询数据
        Response<ActivityAccountQuotaResponseDto> response = raffleActivityService.userAccountQuota(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryRaffleStrategyRuleWeight() {
        RaffleStrategyRuleWeightRequestDto request = new RaffleStrategyRuleWeightRequestDto();
        request.setUserId("xiaofuge");
        request.setActivityId(100301L);
        Response<List<RaffleStrategyRuleWeightResponseDto>> response = raffleActivityService.queryRaffleStrategyRuleWeight(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }
}
