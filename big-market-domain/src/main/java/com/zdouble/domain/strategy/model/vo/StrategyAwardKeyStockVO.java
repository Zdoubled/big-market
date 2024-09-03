package com.zdouble.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 策略奖品key值对象信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyAwardKeyStockVO {
    Long strategyId;
    Integer awardId;
}
