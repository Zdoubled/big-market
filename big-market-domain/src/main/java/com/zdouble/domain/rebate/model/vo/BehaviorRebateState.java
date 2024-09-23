package com.zdouble.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BehaviorRebateState {

    open("open", "行为返利已开启"),
    close("close", "行为返利已关闭"),
    ;

    private final String code;
    private final String info;
}
