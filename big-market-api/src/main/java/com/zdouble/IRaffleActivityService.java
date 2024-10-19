package com.zdouble;

import com.zdouble.dto.*;
import com.zdouble.types.model.Response;

import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 查询用户是否已经签到
     * @param userId
     * @return
     */
    Response<Boolean> isCalendarSignRebate(String userId);

    /**
     * 用户额度查询接口
     * @param activityAccountQuotaRequestDto
     * @return
     */
    Response<ActivityAccountQuotaResponseDto> userAccountQuota(ActivityAccountQuotaRequestDto activityAccountQuotaRequestDto);

    /**
     * 积分兑换接口
     * @param dto
     * @return
     */
    Response<Boolean> creditPayExchangeSku(SkuProductShopCartRequestDTO dto);

    /**
     * 查询活动所有可兑换sku
     * @param activityId
     * @return
     */
    Response<List<SkuProductResponseDto>> querySkuProductListByActivityId(Long activityId);

    /**
     * 查询用户积分账户可用积分
     * @param userId
     * @return
     */
    Response<BigDecimal> queryUserCreditAccount(String userId);
}
