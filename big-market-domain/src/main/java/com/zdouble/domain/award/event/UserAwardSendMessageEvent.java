package com.zdouble.domain.award.event;

import com.zdouble.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserAwardSendMessageEvent extends BaseEvent<UserAwardSendMessageEvent.SendAwardMessage> {
    @Value("${spring.rabbitmq.topic.award_send}")
    private String topic;

    @Override
    public BaseEvent.EventMessage<SendAwardMessage> buildEventMessage(SendAwardMessage sendAwardMessageEvent) {
        return BaseEvent.EventMessage.<SendAwardMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .data(sendAwardMessageEvent)
                .timestamp(new Date())
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendAwardMessage{
        // 用户id
        private String userId;
        // 奖品id
        private Integer awardId;
        // 奖品标题
        private String awardTitle;
    }

}
