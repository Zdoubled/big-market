package com.zdouble.trigger.http;

import com.zdouble.IRaffleActivityService;
import com.zdouble.domain.activity.model.entity.*;
import com.zdouble.domain.activity.model.pojo.OrderTradeTypeVO;
import com.zdouble.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.zdouble.domain.activity.service.IRaffleActivityPartakeService;
import com.zdouble.domain.activity.service.armory.IActivityArmory;
import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;
import com.zdouble.domain.award.model.vo.AwardStateVO;
import com.zdouble.domain.award.service.IAwardService;
import com.zdouble.domain.credit.model.entity.TradeEntity;
import com.zdouble.domain.credit.model.vo.TradeNameVO;
import com.zdouble.domain.credit.model.vo.TradeTypeVO;
import com.zdouble.domain.credit.service.ICreditService;
import com.zdouble.domain.rebate.IBehaviorRebateService;
import com.zdouble.domain.rebate.model.entity.UserBehaviorEntity;
import com.zdouble.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.zdouble.domain.rebate.model.vo.BehaviorTypeVO;
import com.zdouble.domain.strategy.IRaffleRule;
import com.zdouble.domain.strategy.model.entity.RaffleAwardEntity;
import com.zdouble.domain.strategy.model.entity.RaffleFactorEntity;
import com.zdouble.domain.strategy.model.vo.RuleWeightVO;
import com.zdouble.domain.strategy.service.IRaffleStrategy;
import com.zdouble.domain.strategy.service.armory.IStrategyArmory;
import com.zdouble.dto.*;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;
import com.zdouble.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    @Resource
    private ICreditService creditService;

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
            log.info("活动抽奖开始 userId：{}, activityId：{}", activityDrawRequestDto.getUserId(), activityDrawRequestDto.getActivityId());
            Long activityId = activityDrawRequestDto.getActivityId();
            String userId = activityDrawRequestDto.getUserId();
            if (null == activityId || StringUtils.isBlank(userId)) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2. 参加抽奖
            UserRaffleOrderEntity userRaffleOrder = raffleActivityPartakeService.createOrder(userId, activityId);
            log.info("参与抽奖，创建抽奖订单:UserRaffleOrder：{}",userRaffleOrder.toString());
            // 3. 执行抽奖
            log.info("执行抽奖 userId：{}, activityId：{}", userId, activityId);
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                            .strategyId(userRaffleOrder.getStrategyId())
                            .userId(userRaffleOrder.getUserId())
                            .strategyId(userRaffleOrder.getStrategyId())
                            .endTime(userRaffleOrder.getEndTime())
                        .build());
            log.info("执行抽奖结果:RaffleAwardEntity：{}",raffleAwardEntity.toString());
            log.info("开始存放中奖结果");
            // 4. 存放中奖记录结果
            UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                            .activityId(activityId)
                            .userId(userId)
                            .orderId(userRaffleOrder.getOrderId())
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardConfig(raffleAwardEntity.getAwardConfig())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardTime(new Date())
                            .awardState(AwardStateVO.create)
                            .strategyId(userRaffleOrder.getStrategyId())
                        .build();
            awardService.saveUserAwardRecord(userAwardRecordEntity);
            log.info("存放中奖记录结果:UserAwardRecordEntity：{}",userAwardRecordEntity.toString());
            // 5. 返回抽奖结果
            return Response.success(ActivityDrawResponseDto.builder()
                    .awardId(raffleAwardEntity.getAwardId())
                    .orderId(userAwardRecordEntity.getOrderId())
                    .awardConfig(raffleAwardEntity.getAwardConfig())
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
        log.info("开始签到 userId：{}", userId);
        // 1. 参数校验
        try {
            if (StringUtils.isBlank(userId)) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }
            // 2. 调用签到返利服务 orderId是行为返利奖品发放的订单id
            log.info("调用签到返利服务 userId：{}", userId);
            List<String> orderIds = behaviorRebateService.createOrder(UserBehaviorEntity.builder()
                    .userId(userId)
                    .behaviorType(BehaviorTypeVO.sign)
                    .build()
            );
            log.info("签到返利服务调用成功 orderIds：{}", orderIds.toString());
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

    @Override
    @RequestMapping(value = "is_calendar_sign_rebate", method = RequestMethod.POST)
    public Response<Boolean> isCalendarSignRebate(@RequestParam String userId) {
        log.info("开始查询用户是否已经签到, userId：{}", userId);
        try {
            // 1. 参数校验
            if (StringUtils.isBlank(userId)) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2. 调用返利查询服务
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String outBusinessNo = sdf.format(new Date());
            List<UserBehaviorRebateOrderEntity> rebateOrders = behaviorRebateService.isCalendarSignRebate(userId, outBusinessNo);
            log.info("结束查询用户是否已经签到,结果为:{}", !rebateOrders.isEmpty());
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(!rebateOrders.isEmpty())
                    .build();
        }catch (AppException e){
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e){
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "query_user_activity_account", method = RequestMethod.POST)
    public Response<ActivityAccountQuotaResponseDto> userAccountQuota(@RequestBody ActivityAccountQuotaRequestDto activityAccountQuotaRequestDto) {
        log.info("开始查询活动用户额度账户,activityId：{},userId：{}", activityAccountQuotaRequestDto.getActivityId(), activityAccountQuotaRequestDto.getUserId());
        // 1. 参数校验
        try {
            if (null == activityAccountQuotaRequestDto) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            ActivityAccountEntity activityAccountEntity = ActivityAccountEntity.builder()
                    .userId(activityAccountQuotaRequestDto.getUserId())
                    .activityId(activityAccountQuotaRequestDto.getActivityId())
                    .build();
            activityAccountEntity = raffleActivityAccountQuotaService.queryActivityAccountQuotaService(activityAccountEntity);
            log.info("结束查询活动用户额度账户,结果为：{}", activityAccountEntity.toString());
            return Response.<ActivityAccountQuotaResponseDto>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ActivityAccountQuotaResponseDto.builder()
                            .totalCount(activityAccountEntity.getTotalCount())
                            .totalCountSurplus(activityAccountEntity.getTotalCountSurplus())
                            .monthCount(activityAccountEntity.getMonthCount())
                            .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                            .dayCount(activityAccountEntity.getDayCount())
                            .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                            .build())
                    .build();
        }catch (AppException e){
            log.error("用户额度查询失败 userId：{}", activityAccountQuotaRequestDto.getUserId(), e);
            return Response.<ActivityAccountQuotaResponseDto>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e){
            log.error("用户额度查询失败 userId：{}", activityAccountQuotaRequestDto.getUserId());
            return Response.<ActivityAccountQuotaResponseDto>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @PostMapping("credit_pay_exchange_sku")
    public Response<Boolean> creditPayExchangeSku(@RequestBody SkuProductShopCartRequestDTO dto) {
        log.info("开始执行积分兑换商品服务，dto：{}", dto.toString());
        try {
            // 参数校验
            if (null == dto || StringUtils.isBlank(dto.getUserId()) || null == dto.getSku()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 根据userId 和 sku  查询数据库是否有未支付的订单id 存在：直接返回旧的订单  不存在：创建新的订单e'e
            ActivitySkuChargeEntity activitySkuChargeEntity = ActivitySkuChargeEntity.builder()
                    .sku(dto.getSku())
                    .userId(dto.getUserId())
                    .outBusinessNo(RandomStringUtils.randomNumeric(14))
                    .orderTradeType(OrderTradeTypeVO.CreditPayPolicy)
                    .build();
            UnpaidActivityOrderEntity unpaidActivityOrderEntity = raffleActivityAccountQuotaService.createSkuRechargeOrder(activitySkuChargeEntity);
            TradeEntity tradeEntity = TradeEntity.builder()
                    .userId(unpaidActivityOrderEntity.getUserId())
                    .outBusinessNo(unpaidActivityOrderEntity.getOutBusinessNo())
                    .tradeType(TradeTypeVO.reverse)
                    .tradeAmount(BigDecimal.ZERO.subtract(unpaidActivityOrderEntity.getPayAmount()))
                    .tradeName(TradeNameVO.Adjust)
                    .build();
            // 调用积分支付接口进行支付
            String creditAdjustOrder = creditService.createCreditAdjustOrder(tradeEntity);
            log.info("结束执行积分兑换商品服务，返回结果：{}", creditAdjustOrder);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(Boolean.TRUE)
                    .build();
        }catch (AppException e){
            log.error("积分兑换失败 userId：{}", dto.getUserId());
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e){
            log.error("积分兑换失败 userId：{}", dto.getUserId());
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @PostMapping("query_sku_product_list_by_activity_id")
    public Response<List<SkuProductResponseDto>> querySkuProductListByActivityId(@RequestParam Long activityId) {
        log.info("开始执行查询活动sku列表服务，activityId：{}", activityId);
        try{
            // 参数校验
            if(null == activityId){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 根据activityId查询sku
            List<SkuProductEntity> skuProductEntities = raffleActivityAccountQuotaService.querySkuProductEntitiesByActivityId(activityId);
            List<SkuProductResponseDto> skuProductResponseDtoList = skuProductEntities.stream().map(skuProductEntity -> {
                SkuProductResponseDto.ActivityCount activityCount = SkuProductResponseDto.ActivityCount.builder()
                        .totalCount(skuProductEntity.getActivityCount().getTotalCount())
                        .monthCount(skuProductEntity.getActivityCount().getMonthCount())
                        .dayCount(skuProductEntity.getActivityCount().getDayCount())
                        .build();
                return SkuProductResponseDto.builder()
                        .sku(skuProductEntity.getSku())
                        .activityId(skuProductEntity.getActivityId())
                        .activityCountId(skuProductEntity.getActivityCountId())
                        .productAmount(skuProductEntity.getProductAmount())
                        .stockCount(skuProductEntity.getStockCount())
                        .stockCountSurplus(skuProductEntity.getStockCountSurplus())
                        .activityCount(activityCount)
                        .build();
            }).collect(Collectors.toList());
            log.info("结束执行查询活动sku列表服务，返回结果：{}", skuProductResponseDtoList);
            return Response.<List<SkuProductResponseDto>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(skuProductResponseDtoList)
                    .build();
        }catch (AppException e){
            log.error("查询活动sku失败 activityId：{}", activityId);
            return Response.<List<SkuProductResponseDto>>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e){
            log.error("查询活动sku失败 activityId：{}", activityId);
            return Response.<List<SkuProductResponseDto>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @PostMapping("query_user_credit_account")
    public Response<BigDecimal> queryUserCreditAccount(@RequestParam String userId) {
        log.info("开始执行查询用户积分账户服务，userId：{}", userId);
        try{
            BigDecimal creditAccount = creditService.queryCreditAvailableByUserId(userId);
            log.info("结束执行查询用户积分账户服务，返回结果：{}", creditAccount);
            return Response.<BigDecimal>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(creditAccount)
                    .build();
        }catch (AppException e){
            log.error("查询用户积分账户失败 userId：{}", userId);
            return Response.<BigDecimal>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e){
            log.error("查询用户积分账户失败 userId：{}", userId);
            e.printStackTrace();
            return Response.<BigDecimal>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
