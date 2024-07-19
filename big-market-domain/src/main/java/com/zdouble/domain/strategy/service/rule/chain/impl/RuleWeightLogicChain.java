package com.zdouble.domain.strategy.service.rule.chain.impl;

import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.domain.strategy.service.armory.IStrategyDispatch;
import com.zdouble.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.zdouble.types.common.Constants.*;

@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyRepository strategyRepository;
    @Resource
    private IStrategyDispatch strategyDispatch;

    private final Long userScore = 0L;
    @Override
    public DefaultLogicChainFactory.StrategyAwardVO logic(Long strategyId, String userId) {
        log.info("责任链过滤，策略ID：{}，用户ID：{}，规则模型：{}", strategyId, userId, ruleModel());
        String ruleValue = strategyRepository.queryStrategyRuleValue(strategyId, ruleModel());
        Map<Long, String> ruleMap = analyzeRuleValue(ruleValue);
        Long ruleScore = ruleMap.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .filter(x -> userScore >= x)
                .findFirst()
                .orElse(null);

        if (null != ruleScore) {
            return DefaultLogicChainFactory.StrategyAwardVO.builder()
                    .awardId(strategyDispatch.getRandomAwardId(strategyId, ruleMap.get(ruleScore)))
                    .logicModel(ruleModel())
                    .build();
        }

        return next().logic(strategyId, userId);
    }

    private Map<Long, String> analyzeRuleValue(String ruleValue) {
        HashMap<Long, String> map = new HashMap<>();
        if (StringUtils.isNotBlank(ruleValue)) {
            String[] ruleWeightGroups = ruleValue.split(SPACE);
            for (String ruleWeightGroup : ruleWeightGroups) {
                String[] ruleValues = ruleWeightGroup.split(COLON);
                if (ruleValues.length < 2) {
                    throw new IllegalArgumentException("rule value format error");
                }
                map.put(Long.parseLong(ruleValues[0]), ruleWeightGroup);
            }
        }

        return map;
    }

    @Override
    protected String ruleModel() {
        return "rule_weight";
    }
}
