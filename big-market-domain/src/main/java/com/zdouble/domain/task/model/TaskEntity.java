package com.zdouble.domain.task.model;

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
    /** 抽奖用户ID */
    private String userId;
    /** 消息ID */
    private String messageId;
    /** mq */
    private String topic;
    /** 消息体 */
    private String message;
    /** 任务状态 */
    private String state;
}
