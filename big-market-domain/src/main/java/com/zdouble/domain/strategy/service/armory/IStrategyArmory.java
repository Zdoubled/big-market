package com.zdouble.domain.strategy.service.armory;

/**
 * 策略装配库，初始化策略计算
 */
public interface IStrategyArmory {


    /**
     * 根据活动id装配抽奖策略
     * @param articleId
     * @return
     */
    Boolean assembleLotteryStrategyByActivityId(Long articleId);
    /**
     * 装配抽奖策略配置「触发的时机可以为活动审核通过后进行调用」
     *
     * @param strategyId 策略ID
     */
    Boolean assembleLotteryStrategy(Long strategyId);
}
