package com.zdouble.domain.credit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditRechargeEntity {
    private String userId;
    private String outBusinessNo;
    private BigDecimal creditRecharge;
}
