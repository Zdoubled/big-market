package com.zdouble.domain.credit.model.entity;

import com.zdouble.domain.award.model.vo.UserCreditAccountStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditAccountEntity {
    private String userId;
    private BigDecimal totalAmount;
    private BigDecimal availableAmount;
    private UserCreditAccountStatusVO accountStatus;
}
