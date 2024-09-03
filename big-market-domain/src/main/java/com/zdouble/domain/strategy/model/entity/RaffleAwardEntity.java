package com.zdouble.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleAwardEntity {
    // 奖品id
    private Integer awardId;
    // 奖品配置信息
    private String awardConfig;
    // 奖品排序顺序号
    private Integer sort;
}
