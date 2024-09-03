package com.zdouble.trigger.job;

import com.zdouble.domain.strategy.model.vo.StrategyAwardKeyStockVO;
import com.zdouble.domain.strategy.service.IRaffleStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component()
public class UpdateAwardStockJob {

    @Resource
    private IRaffleStock raffleStock;

    /**
     * 更新奖品库存
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void updateAwardStock(){
        try {
            log.info("执行消费奖品队列，更新奖品库存任务");
            StrategyAwardKeyStockVO strategyAwardKeyStockVO = raffleStock.takeQueueValue();
            if (null == strategyAwardKeyStockVO) return;
            raffleStock.updateStrategyAwardStock(strategyAwardKeyStockVO.getStrategyId(), strategyAwardKeyStockVO.getAwardId());
        }catch (Exception e){
            log.error("执行消费奖品队列，更新奖品库存任务异常", e);
        }
    }

}
