package com.zdouble.domain.credit.model.entity;

import com.zdouble.domain.activity.event.CreditAdjustSuccessMessageEvent;
import com.zdouble.domain.credit.model.vo.TaskStateVO;
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
    private BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> message;
    private TaskStateVO state;

}
