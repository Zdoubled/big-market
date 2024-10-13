package com.zdouble.domain.credit.aggregate;

import com.zdouble.domain.credit.model.entity.UserCreditAccountEntity;
import com.zdouble.domain.credit.model.entity.UserCreditOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditRechargeAggregate {
    private String userId;
    private UserCreditAccountEntity userCreditAccountEntity;
    private UserCreditOrderEntity userCreditOrderEntity;
}
