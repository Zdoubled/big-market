package com.zdouble.domain.credit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeNameVO {

    Rebate("行为返利"),

    Adjust("兑换奖品"),
    ;

    private String code;
}
