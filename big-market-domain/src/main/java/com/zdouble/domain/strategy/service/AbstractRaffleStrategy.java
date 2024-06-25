package com.zdouble.domain.strategy.service;

import com.zdouble.domain.strategy.model.entity.*;
import com.zdouble.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.zdouble.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.domain.strategy.service.IRaffleStrategy;
import com.zdouble.domain.strategy.service.armory.IStrategyDispatch;
import com.zdouble.domain.strategy.service.rule.chain.ILogicChain;
import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import com.zdouble.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import com.zdouble.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;

import static com.zdouble.types.enums.ResponseCode.ILLEGAL_PARAMETER;

public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    protected IStrategyRepository strategyRepository;

    protected IStrategyDispatch strategyDispatch;

    protected DefaultLogicChainFactory defaultLogicChainFactory;

    public AbstractRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultLogicChainFactory defaultLogicChainFactory) {
        this.strategyRepository = strategyRepository;
        this.strategyDispatch = strategyDispatch;
        this.defaultLogicChainFactory = defaultLogicChainFactory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        //参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (null == strategyId || StringUtils.isBlank(userId)){
            throw new AppException(ILLEGAL_PARAMETER.getCode(), ILLEGAL_PARAMETER.getInfo());
        }
        //1.获取策略
        StrategyEntity strategyEntity = strategyRepository.queryStrategyByStrategyId(strategyId);
        /**
         * 前置规则过滤 配合责任链模式实现
         */
        ILogicChain logicChain = defaultLogicChainFactory.openLogicChain(strategyId);
        Integer awardId = logicChain.logic(strategyId, userId);

        /**
         * 中置规则过滤
         */
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = strategyRepository.queryStrategyAwardRuleModel(strategyId, awardId);
        RaffleActionEntity<RaffleActionEntity.RaffleCenterAction> actionEntity = this.doCheckRaffleCenterLogic(RaffleFactorEntity.builder()
                .strategyId(strategyId)
                .userId(userId)
                .awardId(awardId)
                .build(), strategyAwardRuleModelVO.raffleCenterRuleModelList());
        if (actionEntity.getCode().equals(RuleLogicCheckTypeVO.ALLOW.getCode())){
            return RaffleAwardEntity.builder()
                    .awardDesc("奖品未解锁，走兜底策略")
                    .build();
        }
        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();
    }

    protected abstract RaffleActionEntity<RaffleActionEntity.RaffleBeforeAction> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels);
    protected abstract RaffleActionEntity<RaffleActionEntity.RaffleCenterAction> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String[] ruleModels);
}
