package com.zdouble.domain.activity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderTradeTypeVO {
    CreditPayPolicy("credit_pay_policy", "需要积分支付"),
    RebateNoPayPolicy("rebate_no_pay_policy", "不需要支付"),
    ;
    private String code;
    private String desc;
}
