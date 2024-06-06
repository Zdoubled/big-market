package com.zdouble.domain.strategy.service.armory;

public interface IStrategyDispatch {
    /**
     * 执行抽奖调度
     * @param strategyId
     * @return
     */
    Integer getRandomAwardId(Long strategyId);

    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);
}
