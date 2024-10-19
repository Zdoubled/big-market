package com.zdouble.domain.strategy.service.armory;

import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.strategy.model.entity.StrategyAwardEntity;
import com.zdouble.domain.strategy.model.entity.StrategyEntity;
import com.zdouble.domain.strategy.model.entity.StrategyRuleEntity;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.types.common.Constants;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

import static com.zdouble.types.enums.ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL;

@Service
@Slf4j
public class StrategyArmory implements IStrategyArmory, IStrategyDispatch {
    @Resource
    private IStrategyRepository strategyRepository;
    @Resource
    private IActivityRepository activityRepository;

    @Override
    public Boolean assembleLotteryStrategyByActivityId(Long articleId) {
        Long strategyId = activityRepository.queryStrategyIdByActivityId(articleId);
        return assembleLotteryStrategy(strategyId);
    }

    @Override
    public Boolean assembleLotteryStrategy(Long strategyId) {
        log.info("策略装配开始 articleId：{}", strategyId);
        //1.获取完整策略配置
        List<StrategyAwardEntity> strategyAwardList = strategyRepository.queryStrategyAwardList(strategyId);
        //2.缓存奖品库存
        for (StrategyAwardEntity strategyAward : strategyAwardList) {
            Integer awardId = strategyAward.getAwardId();
            Integer awardCount = strategyAward.getAwardCount();
            cacheStrategyAwardCount(strategyId,awardId,awardCount);
        }
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwardList);
        //3.默认全量装配配置
        StrategyEntity strategyEntity = strategyRepository.queryStrategyByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (null == ruleWeight) {
            return true;
        }
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId,ruleWeight);
        if (null == strategyRuleEntity) {
            throw new AppException(STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        //4.解析策略权重规则
        Map<String, List<Integer>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        //5.装配
        ruleWeightValues.forEach((key, value) -> {
            ArrayList<StrategyAwardEntity> strategyAwardListClone = new ArrayList<>(strategyAwardList);
            strategyAwardListClone.removeIf(strategyAwardEntity -> !value.contains(strategyAwardEntity.getAwardId()));
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key), strategyAwardListClone);
        });
        log.info("策略装配结束 articleId：{}", strategyId);
        return true;
    }

    private void cacheStrategyAwardCount(Long strategyId, Integer awardId, Integer awardCount) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        strategyRepository.cacheStrategyAwardCount(cacheKey,awardCount);
    }

    public void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardList) {
        //1.获取概率最小值
        BigDecimal minStrategyRate = strategyAwardList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        //2.获取概率总和
/*        BigDecimal sumStrategyRate = strategyAwardList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);*/
        //3.计算概率总范围
        //BigDecimal rateRange = sumStrategyRate.divide(minStrategyRate, 0, RoundingMode.CEILING);
        BigDecimal rateRange = BigDecimal.valueOf(convert(minStrategyRate.doubleValue()));

        //4.初始化抽奖奖品概率范围
        ArrayList<Integer> strategyAwardSearchRateTable = new ArrayList<>(rateRange.intValue());
        strategyAwardList.forEach(strategyAward -> {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();
            //填充
            for (int i=0; i<awardRate.multiply(rateRange).intValue(); i++) {
                strategyAwardSearchRateTable.add(awardId);
            }
        });
        //5.乱序
        Collections.shuffle(strategyAwardSearchRateTable);
        //6.填充抽奖奖品概率范围表
        HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTable = new HashMap<>(strategyAwardSearchRateTable.size());
        for (int i = 0; i < strategyAwardSearchRateTable.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTable.get(i));
        }
        //7.缓存到redis中
        strategyRepository.setStrategyAwardSearchRateTable(key, strategyAwardSearchRateTable.size(), shuffleStrategyAwardSearchRateTable);
    }


    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = strategyRepository.getRateRange(strategyId);
        return strategyRepository.getStrategyAwardAssemble(String.valueOf(strategyId),new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        int rateRange = strategyRepository.getRateRange(key);
        return strategyRepository.getStrategyAwardAssemble(key,new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Boolean subtractAwardCount(Long strategyId, Integer awardId, Date endTime) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        return strategyRepository.subtractAwardCount(cacheKey, endTime);
    }

    /**
     * 转换计算，只根据小数位来计算。如【0.01返回100】、【0.009返回1000】、【0.0018返回10000】
     */
    private double convert(double min) {
        if(0 == min) return 1D;

        double current = min;
        double max = 1;
        while (current < 1) {
            current = current * 10;
            max = max * 10;
        }
        return max;
    }

}
