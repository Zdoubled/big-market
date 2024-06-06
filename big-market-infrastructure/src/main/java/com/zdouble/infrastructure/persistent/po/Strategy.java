package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class Strategy {
    private Long id;
    private Long strategyId;
    private String ruleModel;
    private String strategyDesc;
    private Date createTime;
    private Date updateTime;
}
