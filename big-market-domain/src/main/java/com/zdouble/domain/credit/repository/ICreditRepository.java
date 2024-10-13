package com.zdouble.domain.credit.repository;

import com.zdouble.domain.credit.aggregate.UserCreditRechargeAggregate;
import com.zdouble.domain.credit.model.entity.UserCreditRechargeEntity;

public interface ICreditRepository {
    void doUserCreditRecharge(UserCreditRechargeAggregate userCreditRechargeAggregate);
}
