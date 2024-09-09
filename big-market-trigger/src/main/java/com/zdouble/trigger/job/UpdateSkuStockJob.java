package com.zdouble.trigger.job;

import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;
import com.zdouble.domain.activity.service.ISkuStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class UpdateSkuStockJob {

    @Resource
    private ISkuStock skuStock;

    /**
     * 更新sku库存
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void updateSkuStock(){
        try {
            log.info("执行消费sku队列，更新sku库存任务");
            ActivitySkuStockVO activitySkuStockVO = skuStock.takeQueueValue();
            if (null == activitySkuStockVO) return;
            skuStock.updateSkuStock(activitySkuStockVO.getSku(), activitySkuStockVO.getActivityId());
        }catch (Exception e){
            log.error("执行消费sku队列，更新sku库存任务异常", e);
        }
    }

}
