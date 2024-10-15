package com.zdouble.domain.activity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStateVO {

    completed("complete","完成"),
    wait_pay("wait_pay","等待支付"),
    ;

    private String code;
    private String desc;

}
