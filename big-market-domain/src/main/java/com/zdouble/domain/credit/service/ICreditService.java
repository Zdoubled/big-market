package com.zdouble.domain.credit.service;

import com.zdouble.domain.credit.model.entity.TradeEntity;

import java.math.BigDecimal;

/**
 * 用户额度积分调度接口
 */
public interface ICreditService {
    String createCreditAdjustOrder(TradeEntity tradeEntity);

    BigDecimal queryCreditAvailableByUserId(String userId);
}
