package com.zdouble.domain.activity.service.quota.policy.impl;

import com.zdouble.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.zdouble.domain.activity.model.pojo.OrderStateVO;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.activity.service.quota.policy.ITradePolicy;
import org.springframework.stereotype.Service;

@Service("credit_pay_policy")
public class CreditPayPolicy implements ITradePolicy {

    private IActivityRepository activityRepository;

    public CreditPayPolicy(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        createQuotaOrderAggregate.setOrderStateVO(OrderStateVO.wait_pay);
        activityRepository.doSaveCreditPayOrder(createQuotaOrderAggregate);
    }
}
