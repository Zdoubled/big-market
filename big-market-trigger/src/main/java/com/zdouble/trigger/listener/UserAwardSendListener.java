package com.zdouble.trigger.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class UserAwardSendListener {

    @Value("${spring.rabbitmq.topic.award_send}")
    private String topic;


    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.award_send}"))
    public void onMessage(String message) {
        try {
            log.info("receive message:{}", message);
        } catch (Exception e) {
            log.error("error", e);
        }
    }
}
