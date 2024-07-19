package com.zdouble.domain.strategy.service.rule.filter.factory;

import com.zdouble.domain.strategy.model.entity.RaffleActionEntity;
import com.zdouble.domain.strategy.service.annotation.LogicStrategy;
import com.zdouble.domain.strategy.service.rule.filter.ILogicFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultLogicFactory {
    public Map<String, ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilters){
        logicFilters.forEach(logicFilter -> {
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logicFilter.getClass(), LogicStrategy.class);
            if (null != strategy){
                logicFilterMap.put(strategy.logicModel().code, logicFilter);
            }
        });
    }

    public <T extends RaffleActionEntity.RaffleEntity> Map<String, ILogicFilter<T>> openLogicFilter(){
        return (Map<String, ILogicFilter<T>>) (Map<?, ?>)logicFilterMap;//返回的过滤器类型是动态的
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WIGHT("rule_weight","【抽奖前规则】根据抽奖权重返回可抽奖范围KEY","before"),
        RULE_BLACKLIST("rule_blacklist","【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回","before"),

        RULE_LOCK("rule_lock", "【抽奖中规则】抽奖锁定规则，超过一定次数解锁奖品","center"),
        RULE_LUCK_AWARD("rule_luck_award", "【抽奖后规则】,保底奖品","after");

        private final String code;
        private final String info;
        private final String type;

        public static Boolean isCenter(String ruleModelCode){
            return "center".equals(LogicModel.valueOf(ruleModelCode.toUpperCase()).getType());
        }
    }
}
