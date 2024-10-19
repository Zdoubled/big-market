package com.zdouble.domain.strategy.service;

import com.zdouble.domain.strategy.model.entity.*;
import com.zdouble.domain.strategy.repository.IStrategyRepository;
import com.zdouble.domain.strategy.service.armory.IStrategyDispatch;
import com.zdouble.domain.strategy.service.rule.chain.factory.DefaultLogicChainFactory;
import com.zdouble.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import static com.zdouble.types.enums.ResponseCode.ILLEGAL_PARAMETER;

@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    protected IStrategyRepository strategyRepository;

    protected IStrategyDispatch strategyDispatch;

    protected DefaultLogicChainFactory defaultLogicChainFactory;

    protected DefaultTreeFactory defaultTreeFactory;

    public AbstractRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultLogicChainFactory defaultLogicChainFactory,DefaultTreeFactory defaultTreeFactory) {
        this.strategyRepository = strategyRepository;
        this.strategyDispatch = strategyDispatch;
        this.defaultLogicChainFactory = defaultLogicChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        //参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (null == strategyId || StringUtils.isBlank(userId)){
            throw new AppException(ILLEGAL_PARAMETER.getCode(), ILLEGAL_PARAMETER.getInfo());
        }
        //前置规则过滤，责任链过滤
        DefaultLogicChainFactory.StrategyAwardVO chainStrategyAwardVO = raffleLogicChain(strategyId, userId);
        if(!DefaultLogicChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(chainStrategyAwardVO.getLogicModel())){
            return buildRaffleAwardEntity(strategyId, chainStrategyAwardVO.getAwardId(), chainStrategyAwardVO.getAwardRuleValue());
        }

        //后置规则过滤，规则树过滤
        DefaultTreeFactory.StrategyAwardVO treeStrategyAwardVO = raffleLogicTree(strategyId, userId, chainStrategyAwardVO.getAwardId(), raffleFactorEntity.getEndTime());

        return buildRaffleAwardEntity(strategyId, treeStrategyAwardVO.getAwardId(), treeStrategyAwardVO.getAwardValue());
    }

    private RaffleAwardEntity buildRaffleAwardEntity(Long strategyId, Integer awardId, String awardConfig){
        StrategyAwardEntity strategyAwardEntity = strategyRepository.queryStrategyAwardEntity(strategyId,awardId);
        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .sort(strategyAwardEntity.getSort())
                .awardConfig(awardConfig)
                .awardTitle(strategyAwardEntity.getAwardTitle())
                .build();
    }

    protected abstract DefaultLogicChainFactory.StrategyAwardVO raffleLogicChain(Long strategyId, String userId);
    protected abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(Long strategyId, String userId, Integer awardId);
    protected abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(Long strategyId, String userId, Integer awardId, Date endTime);
}
