package com.zdouble.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleFactorEntity {
    private Long strategyId;
    private String userId;
    private Integer awardId;
    /** 活动结束时间 */
    private Date endTime;
}
