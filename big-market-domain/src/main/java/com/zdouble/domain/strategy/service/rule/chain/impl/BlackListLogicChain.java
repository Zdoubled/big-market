package com.zdouble.domain.strategy.service.rule.chain.impl;

import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.zdouble.types.common.Constants.COLON;
import static com.zdouble.types.common.Constants.SPLIT;

@Slf4j
@Component("rule_blacklist")
public class BlackListLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyRepository strategyRepository;
    @Override
    public DefaultLogicChainFactory.StrategyAwardVO logic(Long strategyId, String userId) {
        log.info("责任链过滤,策略id:{},用户id:{},规则模型:{}",strategyId,userId,ruleModel());
        String ruleValue = strategyRepository.queryStrategyRuleValue(strategyId, ruleModel());
        String[] ruleValues = ruleValue.split(COLON);
        String[] blackList = ruleValues[1].split(SPLIT);

        //2.判断用户是否在黑名单
        for (String black : blackList) {
            if(userId.equals(black)){
                return DefaultLogicChainFactory.StrategyAwardVO.builder()
                        .awardId(Integer.parseInt(ruleValues[0]))
                        .logicModel(ruleModel())
                        .build();
            }
        }
        return next().logic(strategyId, userId);
    }

    @Override
    public String ruleModel() {
        return "rule_blacklist";
    }
}
