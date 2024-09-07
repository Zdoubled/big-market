package com.zdouble.domain.activity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityStateVO {

    create("创建",""),
    start("开始",""),
    end("结束",""),
    close("关闭","")
    ;

    private String code;
    private String desc;
}
