package com.zdouble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyAwardListResponseDto {
    // 奖品id
    private Integer awardId;
    // 奖品标题
    private String awardTitle;
    // 奖品副标题
    private String awardSubTitle;
    // 奖品排序位置
    private Integer sort;
    // 奖品是否解锁字段 true 解锁; false 未解锁
    private Boolean isAwardUnLock;
    // 奖品解锁所需抽奖次数 -- 抽奖N次后解锁，未配置则为空
    private Integer awardRuleLockCount;
    // 奖品解锁剩余抽奖次数 -- 规定的抽奖N次解锁减去用户已经抽奖次数
    private Integer waitUnLockCount;
}
