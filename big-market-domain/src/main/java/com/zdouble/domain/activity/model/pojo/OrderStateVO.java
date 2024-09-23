package com.zdouble.domain.activity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStateVO {

    created("create","创建"),
    completed("complete","完成"),
    ;

    private String code;
    private String desc;

}
