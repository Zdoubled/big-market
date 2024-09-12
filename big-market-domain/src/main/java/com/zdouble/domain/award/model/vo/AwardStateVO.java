package com.zdouble.domain.award.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AwardStateVO {
    create("create", "创建"),
    complete("complete", "发奖完成")
    ;

    private String code;
    private String info;
}
