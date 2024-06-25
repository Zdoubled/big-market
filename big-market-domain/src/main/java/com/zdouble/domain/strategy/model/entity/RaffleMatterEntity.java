package com.zdouble.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleMatterEntity {
    private Long strategyId;
    private String userId;
    private Integer awardId;
    private String ruleModel;
}
