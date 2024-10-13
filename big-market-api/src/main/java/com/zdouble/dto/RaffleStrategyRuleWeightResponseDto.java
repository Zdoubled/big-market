package com.zdouble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleStrategyRuleWeightResponseDto {
    // 当前权重规则抽奖次数
    private Integer ruleWeightCount;
    // 当前用户在这个活动总抽奖次数
    private Integer userActivityAccountTotalUseCount;
    // 当前权重可抽奖范围
    private List<AwardVO> awardVOS;

    @Data
    @Builder
    public static class AwardVO {
        private Integer awardId;
        private String awardTitle;
    }
}
