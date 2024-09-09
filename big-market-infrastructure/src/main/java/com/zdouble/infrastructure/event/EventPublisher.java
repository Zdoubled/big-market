package com.zdouble.infrastructure.event;

import com.alibaba.fastjson.JSON;
import com.zdouble.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
@Component
public class EventPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String topic, BaseEvent.EventMessage<?> eventMessage) {
        try {
            log.info("发送MQ消息 topic:{} message: {}", topic, JSON.toJSONString(eventMessage));
            rabbitTemplate.convertAndSend(topic, JSON.toJSONString(eventMessage));
        } catch (Exception e) {
            log.error("发送MQ消息失败 topic:{} message: {}", topic, JSON.toJSONString(eventMessage), e);
            throw e;
        }
    }
}
