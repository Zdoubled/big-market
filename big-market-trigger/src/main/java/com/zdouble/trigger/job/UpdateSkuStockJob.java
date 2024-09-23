package com.zdouble.trigger.job;

import com.zdouble.domain.activity.model.entity.ActivitySkuEntity;
import com.zdouble.domain.activity.model.pojo.ActivitySkuStockVO;
import com.zdouble.domain.activity.service.IRaffleActivitySkuStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class UpdateSkuStockJob {

    @Resource
    private IRaffleActivitySkuStockService skuStock;
    @Resource
    private ThreadPoolExecutor executor;
    /**
     * 更新sku库存
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void updateSkuStock(){
        try {
            log.info("执行消费sku队列，更新sku库存任务");
            List<ActivitySkuEntity> activitySkuEntities = skuStock.queryActivitySkuList();
            for (ActivitySkuEntity activitySkuEntity : activitySkuEntities) {
                executor.execute(() ->{
                    try {
                        ActivitySkuStockVO activitySkuStockVO = skuStock.takeQueueValue(activitySkuEntity.getSku());
                        if (null == activitySkuStockVO) return;
                        skuStock.updateSkuStock(activitySkuStockVO.getSku(), activitySkuStockVO.getActivityId());
                    }catch (Exception e){
                        log.error("执行消费sku队列，更新sku库存任务异常", e);
                    }
                });
            }
        }catch (Exception e){
            log.error("执行消费sku队列，更新sku库存任务异常", e);
        }
    }

}
