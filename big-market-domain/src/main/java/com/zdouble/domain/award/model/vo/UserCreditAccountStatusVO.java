package com.zdouble.domain.award.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserCreditAccountStatusVO {
    open("open", "可用"),
    close("close", "冻结"),
    ;
    private String code;
    private String desc;
}
