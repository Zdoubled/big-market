package com.zdouble.domain.activity.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRaffleOrderStateVO {
    create("create",""),
    used("used",""),
    close("close","")
    ;

    private String code;
    private String desc;
}
