package com.zdouble.test;

import com.alibaba.fastjson.JSON;
import com.zdouble.IRaffleActivityService;
import com.zdouble.dto.ActivityDrawRequestDto;
import com.zdouble.dto.ActivityDrawResponseDto;
import com.zdouble.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
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
}
