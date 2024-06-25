package com.zdouble.domain.strategy.service.raffle;

import com.zdouble.domain.strategy.model.entity.RaffleActionEntity;
import com.zdouble.domain.strategy.model.entity.RaffleFactorEntity;
import com.zdouble.domain.strategy.model.entity.RaffleMatterEntity;
import com.zdouble.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.domain.strategy.service.AbstractRaffleStrategy;
import com.zdouble.domain.strategy.service.annotation.LogicStrategy;
import com.zdouble.domain.strategy.service.armory.IStrategyDispatch;
import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import com.zdouble.domain.strategy.service.rule.filter.ILogicFilter;
import com.zdouble.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {

    @Autowired
    private DefaultLogicFactory logicFactory;

    public DefaultRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultLogicChainFactory defaultLogicChainFactory) {
        super(strategyRepository, strategyDispatch, defaultLogicChainFactory);
    }


    @Override
    protected RaffleActionEntity<RaffleActionEntity.RaffleBeforeAction> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels) {
        Map<String, ILogicFilter<RaffleActionEntity.RaffleBeforeAction>> logicFilters = logicFactory.openLogicFilter();
        //1.规则判断，黑名单优先
        String blackListLogic = Arrays.stream(ruleModels)
                .filter(ruleModel -> ruleModel.contains(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);

        if (StringUtils.isNotBlank(blackListLogic)){
            ILogicFilter<RaffleActionEntity.RaffleBeforeAction> blackListLogicFilter = logicFilters.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RaffleActionEntity<RaffleActionEntity.RaffleBeforeAction> actionEntity = blackListLogicFilter.doFilter(RaffleMatterEntity.builder()
                    .strategyId(raffleFactorEntity.getStrategyId())
                    .userId(raffleFactorEntity.getUserId())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                    .build());
            if (actionEntity.getCode().equals(RuleLogicCheckTypeVO.TAKE_OVER.getCode())){
                return actionEntity;
            }
        }
        List<String> logics = Arrays.stream(ruleModels)
                .filter(ruleModel -> !ruleModel.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());

        for (String logic : logics) {
            if (StringUtils.isNotBlank(logic)){
                ILogicFilter<RaffleActionEntity.RaffleBeforeAction> logicFilter = logicFilters.get(logic);
                RaffleActionEntity<RaffleActionEntity.RaffleBeforeAction> actionEntity = logicFilter.doFilter(RaffleMatterEntity.builder()
                        .strategyId(raffleFactorEntity.getStrategyId())
                        .userId(raffleFactorEntity.getUserId())
                        .ruleModel(logic)
                        .build());
                if (actionEntity.getCode().equals(RuleLogicCheckTypeVO.TAKE_OVER.getCode())){
                    return actionEntity;
                }
            }
        }

        return RaffleActionEntity.<RaffleActionEntity.RaffleBeforeAction>builder()
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .build();
    }

    @Override
    protected RaffleActionEntity<RaffleActionEntity.RaffleCenterAction> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels) {

        Map<String, ILogicFilter<RaffleActionEntity.RaffleCenterAction>> logicFilters = logicFactory.openLogicFilter();
        for (String logic : ruleModels) {
            if (logic.equals(DefaultLogicFactory.LogicModel.RULE_LOCK.getCode())){
                ILogicFilter<RaffleActionEntity.RaffleCenterAction> logicFilter = logicFilters.get(logic);
                RaffleActionEntity<RaffleActionEntity.RaffleCenterAction> actionEntity = logicFilter.doFilter(RaffleMatterEntity.builder()
                        .strategyId(raffleFactorEntity.getStrategyId())
                        .userId(raffleFactorEntity.getUserId())
                        .awardId(raffleFactorEntity.getAwardId())
                        .ruleModel(logic)
                        .build());
                if (actionEntity.getCode().equals(RuleLogicCheckTypeVO.TAKE_OVER.getCode())){
                    return actionEntity;
                }
            }
        }
        return RaffleActionEntity.<RaffleActionEntity.RaffleCenterAction>builder()
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .build();
    }
}
