package com.zdouble.domain.strategy.service.rule.filter.impl;

import com.zdouble.domain.strategy.model.entity.RaffleActionEntity;
import com.zdouble.domain.strategy.model.entity.RaffleMatterEntity;
import com.zdouble.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.domain.strategy.service.annotation.LogicStrategy;
import com.zdouble.domain.strategy.service.rule.filter.ILogicFilter;
import com.zdouble.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.zdouble.types.enums.ResponseCode.ILLEGAL_PARAMETER;

@Component
@Slf4j
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RaffleActionEntity.RaffleCenterAction> {
    @Autowired
    private IStrategyRepository strategyRepository;

    private Long userRaffleTimes = 0L;

    @Override
    public RaffleActionEntity<RaffleActionEntity.RaffleCenterAction> doFilter(RaffleMatterEntity entity) {
        //1.参数校验
        Long strategyId = entity.getStrategyId();
        String userId = entity.getUserId();
        if (null == strategyId || StringUtils.isBlank(userId)) {
            throw new AppException(ILLEGAL_PARAMETER.getCode(), ILLEGAL_PARAMETER.getInfo());
        }
        //2.根据策略id和规则模型获取抽奖锁定规则
        String ruleLockValueStr = strategyRepository.queryStrategyRuleValue(strategyId, entity.getAwardId(), entity.getRuleModel());
        if (StringUtils.isBlank(ruleLockValueStr)) {
            return RaffleActionEntity.<RaffleActionEntity.RaffleCenterAction>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .ruleModel(entity.getRuleModel())
                    .build();
        }
        Long ruleLockValue = Long.valueOf(ruleLockValueStr);
        if (userRaffleTimes >= ruleLockValue) {
            return RaffleActionEntity.<RaffleActionEntity.RaffleCenterAction>builder()
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .data(RaffleActionEntity.RaffleCenterAction.builder()
                            .strategyId(strategyId)
                            .awardId(entity.getAwardId())
                            .ruleLockValue(ruleLockValueStr)
                            .build()
                    )
                    .ruleModel(entity.getRuleModel())
                    .build();
        }
        return RaffleActionEntity.<RaffleActionEntity.RaffleCenterAction>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .ruleModel(entity.getRuleModel())
                .build();
    }
}
