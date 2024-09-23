package com.zdouble;

import com.zdouble.dto.ActivityDrawRequestDto;
import com.zdouble.dto.ActivityDrawResponseDto;
import com.zdouble.types.model.Response;

public interface IRaffleActivityService {

    /**
     * 装配活动、活动奖品缓存预热
     * @param activityId
     * @return
     */
    Response<Boolean> activityArmory(Long activityId);

    /**
     * 活动抽奖接口
     * @param activityDrawRequestDto
     * @return
     */
    Response<ActivityDrawResponseDto> draw(ActivityDrawRequestDto activityDrawRequestDto);

    /**
     * 用户签到行为返利接口
     * @param userId
     * @return
     */
    Response<Boolean> calendarSignRebate(String userId);
}
