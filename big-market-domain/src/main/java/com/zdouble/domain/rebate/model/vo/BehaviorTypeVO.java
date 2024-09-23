package com.zdouble.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {

    sign("sign", "签到"),
    ;
    private final String code;
    private final String info;
}
