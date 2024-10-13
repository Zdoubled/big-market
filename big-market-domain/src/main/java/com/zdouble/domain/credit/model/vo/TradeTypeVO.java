package com.zdouble.domain.credit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeTypeVO {

    forward("forward", "正向"),
    reverse("reverse", "反向");


    private String code;
    private String desc;
}
