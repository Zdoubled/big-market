package com.zdouble.trigger.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zdouble.domain.award.event.UserAwardSendMessageEvent;
import com.zdouble.domain.award.model.entity.DistributeAwardEntity;
import com.zdouble.domain.award.service.IAwardService;
import com.zdouble.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class UserAwardSendListener {

    @Value("${spring.rabbitmq.topic.award_send}")
    private String topic;
    @Resource
    private IAwardService awardService;


    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.award_send}"))
    public void onMessage(String message) {
        try {
            BaseEvent.EventMessage<UserAwardSendMessageEvent.SendAwardMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<UserAwardSendMessageEvent.SendAwardMessage>>() {
            }.getType());
            UserAwardSendMessageEvent.SendAwardMessage sendAwardMessage = eventMessage.getData();
            DistributeAwardEntity distributeAwardEntity = DistributeAwardEntity.builder()
                    .userId(sendAwardMessage.getUserId())
                    .awardId(sendAwardMessage.getAwardId())
                    .orderId(sendAwardMessage.getOrderId())
                    .awardConfig(sendAwardMessage.getAwardConfig())
                    .build();
            awardService.giveOutPrizes(distributeAwardEntity);
        } catch (Exception e) {
            log.error("奖品下发异常", e);
        }
    }
}
