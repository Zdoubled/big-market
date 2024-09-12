package com.zdouble.trigger.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zdouble.domain.award.event.UserAwardSendMessageEvent;
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
            // 调用奖品下发接口，实现奖品的发放
            UserAwardSendMessageEvent.SendAwardMessage sendAwardMessage = JSON.parseObject(message, new TypeReference<UserAwardSendMessageEvent.SendAwardMessage>() {
            });
            // awardService.sendAward(sendAwardMessage);
            log.info("奖品下发成功, {}", sendAwardMessage);
        } catch (Exception e) {
            log.error("奖品下发异常", e);
        }
    }
}
