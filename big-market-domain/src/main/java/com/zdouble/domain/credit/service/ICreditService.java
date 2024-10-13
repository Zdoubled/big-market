package com.zdouble.domain.credit.service;

import com.zdouble.domain.credit.model.entity.UserCreditRechargeEntity;

/**
 * 用户额度积分调度接口
 */
public interface ICreditService {
    String createCreditRechargeOrder(UserCreditRechargeEntity userCreditRechargeEntity);
}
