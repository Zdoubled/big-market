package com.zdouble.domain.credit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrderEntity {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 业务仿重ID
     */
    private String outBusinessNo;
}
