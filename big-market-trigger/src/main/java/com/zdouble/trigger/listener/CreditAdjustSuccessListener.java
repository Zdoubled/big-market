package com.zdouble.trigger.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zdouble.domain.activity.event.CreditAdjustSuccessMessageEvent;
import com.zdouble.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.zdouble.domain.credit.model.entity.DeliveryOrderEntity;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.event.BaseEvent;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class CreditAdjustSuccessListener {

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    @Value("${spring.rabbitmq.topic.credit_adjust_success}")
    private String topic;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.credit_adjust_success}"))
    public void onMessage(String message) {
        try {
            BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage>>() {
            }.getType());
            CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage eventMessageData = eventMessage.getData();

            // 积分发货
            DeliveryOrderEntity deliveryOrderEntity = DeliveryOrderEntity.builder()
                    .userId(eventMessageData.getUserId())
                    .outBusinessNo(eventMessageData.getOutBusinessNo())
                    .build();
            raffleActivityAccountQuotaService.updateOrder(deliveryOrderEntity);
            log.info("监听积分账户调整成功消息，进行交易商品发货 topic: {} message: {}", topic, message);
        } catch (AppException e) {
            if (ResponseCode.INDEX_DUP.getCode().equals(e.getCode())) {
                log.warn("监听积分账户调整成功消息，进行交易商品发货，消费重复 topic: {} message: {}", topic, message, e);
                return;
            }
            throw e;
        }catch (Exception e) {
            log.error("监听积分账户调整成功消息，进行交易商品发货失败 topic: {} message: {}", topic, message, e);
            throw e;
        }
    }
}
