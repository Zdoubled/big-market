package com.zdouble.trigger.http;

import com.zdouble.IRaffleActivityService;
import com.zdouble.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.zdouble.domain.activity.model.entity.UserRaffleOrderEntity;
import com.zdouble.domain.activity.service.IRaffleActivityPartakeService;
import com.zdouble.domain.activity.service.armory.IActivityArmory;
import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;
import com.zdouble.domain.award.model.vo.AwardStateVO;
import com.zdouble.domain.award.service.IAwardService;
import com.zdouble.domain.rebate.IBehaviorRebateService;
import com.zdouble.domain.rebate.model.entity.UserBehaviorEntity;
import com.zdouble.domain.rebate.model.vo.BehaviorTypeVO;
import com.zdouble.domain.strategy.model.entity.RaffleAwardEntity;
import com.zdouble.domain.strategy.model.entity.RaffleFactorEntity;
import com.zdouble.domain.strategy.service.IRaffleStrategy;
import com.zdouble.domain.strategy.service.armory.IStrategyArmory;
import com.zdouble.dto.ActivityDrawRequestDto;
import com.zdouble.dto.ActivityDrawResponseDto;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;
import com.zdouble.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity/")
public class RaffleControllerActivity implements IRaffleActivityService {

    @Resource
    private IActivityArmory activityArmory;
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;
    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private IAwardService awardService;
    @Resource
    private IBehaviorRebateService behaviorRebateService;


    @Override
    @RequestMapping(value = "armory", method = RequestMethod.GET)
    public Response<Boolean> activityArmory(@RequestParam Long activityId) {
        try {
            if (null == activityId) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 1. 活动装配
            activityArmory.assembleActivitySkuByActivityId(activityId);
            // 2. 策略装配
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);
            return Response.success(Boolean.TRUE);
        }catch (Exception e){
            log.error("活动装配失败 activityId：{}", activityId, e);
            return Response.fail("活动装配失败", Boolean.FALSE);
        }
    }

    @Override
    @RequestMapping(value = "draw", method = RequestMethod.POST)
    public Response<ActivityDrawResponseDto> draw(@RequestBody ActivityDrawRequestDto activityDrawRequestDto) {
        // 1. 参数校验
        try {
            Long activityId = activityDrawRequestDto.getActivityId();
            String userId = activityDrawRequestDto.getUserId();
            if (null == activityId || StringUtils.isBlank(userId)) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2. 参加抽奖
            UserRaffleOrderEntity userRaffleOrder = raffleActivityPartakeService.createOrder(userId, activityId);
            // 3. 执行抽奖
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                            .strategyId(userRaffleOrder.getStrategyId())
                            .userId(userRaffleOrder.getUserId())
                            .strategyId(userRaffleOrder.getStrategyId())
                            .endTime(userRaffleOrder.getEndTime())
                        .build());
            // 4. 存放中奖记录结果
            UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                            .activityId(activityId)
                            .userId(userId)
                            .orderId(userRaffleOrder.getOrderId())
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardTime(new Date())
                            .awardState(AwardStateVO.create)
                            .strategyId(userRaffleOrder.getStrategyId())
                        .build();
            awardService.saveUserAwardRecord(userAwardRecordEntity);
            // 5. 返回抽奖结果
            return Response.success(ActivityDrawResponseDto.builder()
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardIndex(raffleAwardEntity.getSort())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .build());
        }catch (AppException e){
            log.error("抽奖失败 activityId：{}", activityDrawRequestDto.getActivityId(), e);
            return Response.<ActivityDrawResponseDto>builder()
                    .info(e.getInfo())
                    .code(e.getCode())
                    .build();
        }catch (Exception e){
            log.error("抽奖失败 activityId：{}", activityDrawRequestDto.getActivityId(), e);
            return Response.<ActivityDrawResponseDto>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "calendar_sign_rebate", method = RequestMethod.POST)
    public Response<Boolean> calendarSignRebate(@RequestParam String userId) {
        // 1. 参数校验
        try {
            if (StringUtils.isBlank(userId)) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }
            // 2. 调用签到返利服务
            List<String> orderIds = behaviorRebateService.createOrder(UserBehaviorEntity.builder()
                    .userId(userId)
                    .behaviorType(BehaviorTypeVO.sign)
                    .build()
            );
            orderIds.forEach(log::info);
            // 3. 返回结果
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(Boolean.TRUE)
                    .build();
        }catch (AppException e){
            log.error("签到失败 userId：{}", userId, e);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .data(Boolean.FALSE)
                    .build();
        }catch (Exception e){
            log.error("签到失败 userId：{}", userId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(Boolean.FALSE)
                    .build();
        }
    }
}
