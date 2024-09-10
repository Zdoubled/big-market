package com.zdouble.domain.activity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStateVO {

    created("创建",""),
    completed("完成",""),
    ;

    private String code;
    private String desc;

}