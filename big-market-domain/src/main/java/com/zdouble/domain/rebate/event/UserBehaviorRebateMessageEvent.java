package com.zdouble.domain.rebate.event;

import com.zdouble.domain.rebate.model.entity.DailyBehaviorRebateEntity;
import com.zdouble.domain.rebate.model.vo.RebateTypeVO;
import com.zdouble.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserBehaviorRebateMessageEvent extends BaseEvent<UserBehaviorRebateMessageEvent.SendUserBehaviorRebateMessage> {

    @Value("${spring.rabbitmq.topic.rebate_send}")
    private String topic;

    @Override
    public EventMessage<SendUserBehaviorRebateMessage> buildEventMessage(SendUserBehaviorRebateMessage data) {
        return EventMessage.<SendUserBehaviorRebateMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendUserBehaviorRebateMessage{
        private String userId;
        private String bizId;
        private String rebateConfig;
        private RebateTypeVO rebateType;
    }
}
