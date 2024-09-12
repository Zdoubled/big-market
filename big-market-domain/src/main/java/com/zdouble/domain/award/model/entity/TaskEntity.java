package com.zdouble.domain.award.model.entity;

import com.zdouble.domain.award.event.UserAwardSendMessageEvent;
import com.zdouble.domain.award.model.vo.TaskStateVO;
import com.zdouble.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    private String userId; // 做库路由
    private String messageId; // 唯一标识
    private String topic;
    private BaseEvent.EventMessage<UserAwardSendMessageEvent.SendAwardMessage> message;
    private TaskStateVO state;
}
