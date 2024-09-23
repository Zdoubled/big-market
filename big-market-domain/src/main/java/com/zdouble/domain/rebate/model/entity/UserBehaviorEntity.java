package com.zdouble.domain.rebate.model.entity;

import com.zdouble.domain.rebate.model.vo.BehaviorTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBehaviorEntity {
    private String userId;
    private BehaviorTypeVO behaviorType;
}
