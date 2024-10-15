package com.zdouble.domain.activity.service.quota.policy;

import com.zdouble.domain.activity.model.aggregate.CreateQuotaOrderAggregate;

public interface ITradePolicy {
    /**
     * 交易服务接口
     */
    void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate);
}
