package com.zdouble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuProductResponseDto {
    private Long sku;
    private Long activityId;
    private Long activityCountId;
    private Integer totalCount;
    private Integer monthCount;
    private Integer dayCount;
    private Integer stockCount;
    private Integer stockCountSurplus;
    private BigDecimal productAmount;
}
