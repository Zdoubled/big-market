package com.zdouble.domain.strategy.service;


import com.zdouble.domain.strategy.model.vo.StrategyAwardKeyStockVO;

/**
 * 库存扣减队列任务消费
 */
public interface IRaffleStock {
    /**
     * 获取队列值
     * @return
     * @throws InterruptedException
     */
    StrategyAwardKeyStockVO takeQueueValue() throws InterruptedException;

    /**
     * 更新数据库奖品库存信息
     * @param strategyId
     * @param awardId
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);
}
