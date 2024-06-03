package com.zdouble.domain.strategy.service.armory;

import com.zdouble.domain.strategy.model.entity.StrategyAwardEntry;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class StrategyArmory implements IStrategyArmory {
    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public void assembleLotteryStrategy(Long strategyId) {
        //1.获取策略配置
        List<StrategyAwardEntry> strategyAwardList = strategyRepository.queryStrategyAwardList(strategyId);
        //2.获取概率最小值
        BigDecimal minStrategyRate = strategyAwardList.stream()
                .map(StrategyAwardEntry::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        log.info("策略概率最小值:{}", minStrategyRate);
        //3.获取概率总和
        BigDecimal sumStrategyRate = strategyAwardList.stream()
                .map(StrategyAwardEntry::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //4.计算概率总范围
        log.info("策略概率总范围:{}", sumStrategyRate);
        BigDecimal rateRange = sumStrategyRate.divide(minStrategyRate, 0, RoundingMode.CEILING);
        log.info("策略概率总范围2:{}", rateRange);

        //5.初始化抽奖奖品概率范围
        ArrayList<Integer> strategyAwardSearchRateTable = new ArrayList<>(rateRange.intValue());
        strategyAwardList.forEach(strategyAward -> {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();
            //填充
            for (int i=0; i<awardRate.multiply(rateRange).intValue(); i++) {
                strategyAwardSearchRateTable.add(awardId);
            }
        });
        //6.乱序
        Collections.shuffle(strategyAwardSearchRateTable);
        //7.填充抽奖奖品概率范围表
        HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTable = new HashMap<>(strategyAwardSearchRateTable.size());
        for (int i = 0; i < strategyAwardSearchRateTable.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTable.get(i));
        }
        //8.缓存到redis中
        strategyRepository.setStrategyAwardSearchRateTable(strategyId, strategyAwardSearchRateTable.size(), shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = strategyRepository.getRateRange(strategyId);
        log.info("rateRange:{}",rateRange);
        return strategyRepository.getStrategyAwardAssemble(strategyId,new SecureRandom().nextInt(rateRange));
    }

}
