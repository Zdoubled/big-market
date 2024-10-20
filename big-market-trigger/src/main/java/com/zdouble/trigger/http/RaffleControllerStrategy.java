package com.zdouble.trigger.http;

import com.alibaba.fastjson.JSON;
import com.zdouble.IRaffleStrategyService;
import com.zdouble.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.zdouble.domain.strategy.IRaffleRule;
import com.zdouble.domain.strategy.model.entity.RaffleAwardEntity;
import com.zdouble.domain.strategy.model.entity.RaffleFactorEntity;
import com.zdouble.domain.strategy.model.entity.StrategyAwardEntity;
import com.zdouble.domain.strategy.service.IRaffleAward;
import com.zdouble.domain.strategy.service.IRaffleStrategy;
import com.zdouble.domain.strategy.service.armory.IStrategyArmory;
import com.zdouble.dto.StrategyAwardListRequestDto;
import com.zdouble.dto.StrategyAwardListResponseDto;
import com.zdouble.dto.StrategyRaffleRequestDto;
import com.zdouble.dto.StrategyRaffleResponseDto;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy/")
public class RaffleControllerStrategy implements IRaffleStrategyService {
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IRaffleAward raffleAward;
    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private IRaffleRule raffleRule;
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    /**
     * 策略装配，将策略信息装配到缓存中
     *
     * @param strategyId 策略ID
     * @return 装配结果
     */
    @RequestMapping(value = "strategy_armory", method = RequestMethod.GET)
    @Override
    public Response<Boolean> strategyArmory(@RequestParam Long strategyId) {
        try {
            log.info("抽奖策略装配开始 strategyId：{}", strategyId);
            Boolean result = strategyArmory.assembleLotteryStrategy(strategyId);
            log.info("抽奖策略装配结束 result：{}", JSON.toJSONString(result));
            return Response.success(result);
        }catch (Exception e){
            log.error("抽奖策略装配失败 strategyId：{}", strategyId, e);
            return Response.fail("抽奖策略装配失败");
        }
    }

    /**
     * 查询奖品列表
     *
     * @param strategyAwardListRequestDto {"strategyId":1000001}
     * @return 奖品列表
     */
    @RequestMapping(value = "query_raffle_award_list", method = RequestMethod.POST)
    @Override
    public Response<List<StrategyAwardListResponseDto>> queryStrategyAwardList(@RequestBody StrategyAwardListRequestDto strategyAwardListRequestDto) {
        try {
            if (null == strategyAwardListRequestDto || null == strategyAwardListRequestDto.getActivityId()) {
                log.info("查询奖品列表失败, 参数不合法。UserId : {}; ActivityId : {} ", strategyAwardListRequestDto.getUserId(), strategyAwardListRequestDto.getActivityId());
                return Response.<List<StrategyAwardListResponseDto>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }
            List<StrategyAwardEntity> strategyAwardEntities = raffleAward.queryRaffleStrategyAwardList(strategyAwardListRequestDto.getActivityId());
            // 查询策略ruleModels
            String[] treeIds = strategyAwardEntities.stream()
                    .map(StrategyAwardEntity::getRuleModels)
                    .filter(StringUtils::isNotBlank)
                    .toArray(String[]::new);
            HashMap<String, Integer> resultMap = raffleRule.queryRuleLockCount(treeIds);
            Integer raffleActivityPartakeCount = raffleActivityAccountQuotaService.queryRaffleActivityTotalPartakeCount(strategyAwardListRequestDto.getUserId(), strategyAwardListRequestDto.getActivityId());

            List<StrategyAwardListResponseDto> result = strategyAwardEntities.stream().map(strategyAwardEntity -> {
                Integer ruleLockCount = resultMap.get(strategyAwardEntity.getRuleModels());
                return StrategyAwardListResponseDto.builder()
                        .awardId(strategyAwardEntity.getAwardId())
                        .awardTitle(strategyAwardEntity.getAwardTitle())
                        .awardSubTitle(strategyAwardEntity.getAwardSubTitle())
                        .sort(strategyAwardEntity.getSort())
                        .awardRuleLockCount(ruleLockCount)
                        .isAwardUnLock(ruleLockCount == null || raffleActivityPartakeCount > ruleLockCount)
                        .waitUnLockCount(ruleLockCount == null || ruleLockCount <= raffleActivityPartakeCount ? 0 : ruleLockCount - raffleActivityPartakeCount)
                        .build();
            }).collect(Collectors.toList());

            return Response.<List<StrategyAwardListResponseDto>>builder().code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
        }catch (Exception e){
            log.error("查询奖品列表失败");
            return Response.<List<StrategyAwardListResponseDto>>builder()
                    .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                    .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                    .build();
        }
    }

    /**
     * 随机抽奖接口
     * <a href="http://localhost:8091/api/v1/raffle/random_raffle">/api/v1/raffle/random_raffle</a>
     *
     * @param strategyRaffleRequestDto 请求参数 {"strategyId":1000001}
     * @return 抽奖结果
     */
    @RequestMapping(value = "random_raffle", method = RequestMethod.POST)
    @Override
    public Response<StrategyRaffleResponseDto> performRaffle(@RequestBody StrategyRaffleRequestDto strategyRaffleRequestDto) {
        try {
            if (null == strategyRaffleRequestDto || null == strategyRaffleRequestDto.getStrategyId()) {
                return Response.fail();
            }
            log.info("执行抽奖:{}", strategyRaffleRequestDto.getStrategyId());
            RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                    .userId("system")
                    .strategyId(strategyRaffleRequestDto.getStrategyId())
                    .build();
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
            StrategyRaffleResponseDto result = StrategyRaffleResponseDto.builder()
                    .awardId(raffleAwardEntity.getAwardId())
                    .sort(raffleAwardEntity.getSort())
                    .build();
            return Response.success(result);
        }catch (Exception e){
            log.error("执行抽奖失败");
            return Response.fail("执行抽奖失败");
        }
    }
}
