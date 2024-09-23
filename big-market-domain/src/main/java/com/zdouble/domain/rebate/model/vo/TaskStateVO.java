package com.zdouble.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStateVO {

    create("create", "创建"),
    completed("completed", "发送完成"),
    fail("fail", "发送失败")
    ;

    private String code;
    private String info;
}