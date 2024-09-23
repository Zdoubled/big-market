package com.zdouble.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RebateTypeVO {
    sku("sku", "活动库存充值商品"),
    integral("integral", "用户活动积分"),
    ;

    private final String code;
    private final String info;
}
