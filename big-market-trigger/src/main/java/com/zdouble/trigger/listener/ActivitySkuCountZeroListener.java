package com.zdouble.trigger.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zdouble.domain.activity.service.ISkuStock;
import com.zdouble.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class ActivitySkuCountZeroListener {

    @Value("${spring.rabbitmq.topic.activity-sku-stock-zero}")
    private String topic;
    @Resource
    private ISkuStock skuStock;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.activity-sku-stock-zero}"))
    public void onMessage(String message) {
        try {
            log.info("监听到消息：{}", message);
            // 1. 类型转换
            BaseEvent.EventMessage<Long> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<Long>>() {
            }.getType());
            Long sku = eventMessage.getData();
            // 3. 更新库存直接位0
            skuStock.updateSkuStockZero(sku);
            // 2. 清理延迟队列
            skuStock.clearQueueValue();
        }catch (Exception e){
            log.error("监听到消息异常：{}", message, e);
            throw e;
        }

    }
}
