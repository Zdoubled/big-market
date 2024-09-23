package com.zdouble.test.trigger;

import com.alibaba.fastjson.JSON;
import com.zdouble.IRaffleActivityService;
import com.zdouble.dto.ActivityDrawRequestDto;
import com.zdouble.dto.ActivityDrawResponseDto;
import com.zdouble.dto.StrategyAwardListRequestDto;
import com.zdouble.dto.StrategyAwardListResponseDto;
import com.zdouble.trigger.http.RaffleControllerStrategy;
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
public class StrategyAwardListTest {

    @Resource
    private IRaffleActivityService raffleActivityService;

    @Test
    public void test_armory(){
        Response<Boolean> response = raffleActivityService.activityArmory(100301L);
        log.info("response : {}", JSON.toJSONString(response));
    }

    @Test
    public void test_draw(){
        ActivityDrawRequestDto dto = ActivityDrawRequestDto.builder().activityId(100301L).userId("xiaofuge").build();
        Response<ActivityDrawResponseDto> draw = raffleActivityService.draw(dto);
        log.info("dto : {}", JSON.toJSONString(dto));
        log.info("draw : {}", JSON.toJSONString(draw));
    }
}
