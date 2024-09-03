package com.zdouble.domain.strategy.service.rule.chain.factory;

import com.zdouble.domain.strategy.model.entity.StrategyEntity;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.domain.strategy.service.rule.chain.ILogicChain;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultLogicChainFactory {

    private final Map<String, ILogicChain> logicChainGroup;

    protected IStrategyRepository strategyRepository;

    public DefaultLogicChainFactory(Map<String, ILogicChain> logicChainGroup, IStrategyRepository strategyRepository) {
        this.logicChainGroup = logicChainGroup;
        this.strategyRepository = strategyRepository;
    }

    public ILogicChain openLogicChain(Long strategyId) {
        StrategyEntity strategyEntity = strategyRepository.queryStrategyByStrategyId(strategyId);
        String[] ruleModels = strategyEntity.getModel();

        if (ruleModels == null || ruleModels.length == 0) {
            return logicChainGroup.get("default");
        }
        ILogicChain head = logicChainGroup.get(ruleModels[0]);
        ILogicChain current = head;
        for (int i = 1; i < ruleModels.length; i++) {
            current = current.appendNext(logicChainGroup.get(ruleModels[i]));
        }
        current.appendNext(logicChainGroup.get("default"));
        return head;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StrategyAwardVO {
        private Integer awardId;
        private String logicModel;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WIGHT("rule_weight","【抽奖前规则】根据抽奖权重返回可抽奖范围KEY","before"),
        RULE_BLACKLIST("rule_blacklist","【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回","before"),

        RULE_LOCK("rule_lock", "【抽奖中规则】抽奖锁定规则，超过一定次数解锁奖品","center"),
        RULE_LUCK_AWARD("rule_luck_award", "【抽奖后规则】,保底奖品","after"),
        RULE_DEFAULT("default", "【抽奖后规则】保底奖品","after");

        private final String code;
        private final String info;
        private final String type;
    }
}
