package com.zdouble.domain.strategy.service.armory;

public interface IStrategyDispatch {
    /**
     * 执行抽奖调度
     * @param strategyId
     * @return
     */
    Integer getRandomAwardId(Long strategyId);

    /**
     * 执行带权重抽奖调度
     * @param strategyId
     * @return
     */
    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

    /**
     * 执行库存扣减
     * @param strategyId
     * @param awardId
     * @return
     */
    Boolean subtractAwardCount(Long strategyId, Integer awardId);
}
