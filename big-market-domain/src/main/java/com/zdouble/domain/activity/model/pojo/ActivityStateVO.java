package com.zdouble.domain.activity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityStateVO {

    create("create",""),
    start("online",""),
    end("offline",""),
    close("close","")
    ;

    private String code;
    private String desc;
}
