package com.zdouble.domain.credit.repository;

import com.zdouble.domain.credit.aggregate.TradeAggregate;

import java.math.BigDecimal;

public interface ICreditRepository {
    void doSaveUserCreditAdjust(TradeAggregate tradeAggregate);

    BigDecimal queryCreditAvailableByUserId(String userId);
}
