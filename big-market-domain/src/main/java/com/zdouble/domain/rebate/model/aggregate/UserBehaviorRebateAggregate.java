package com.zdouble.domain.rebate.model.aggregate;

import com.zdouble.domain.rebate.model.entity.TaskEntity;
import com.zdouble.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBehaviorRebateAggregate {
    private String userId;
    private TaskEntity taskEntity;
    private UserBehaviorRebateOrderEntity userBehaviorRebateOrder;
}
