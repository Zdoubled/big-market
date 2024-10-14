package com.zdouble.domain.credit.repository;

import com.zdouble.domain.credit.aggregate.TradeAggregate;

public interface ICreditRepository {
    void doSaveUserCreditAdjust(TradeAggregate tradeAggregate);
}
