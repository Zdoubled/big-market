package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

@Data
public class Strategy {
    private Long id;
    private String strategyId;
    private String strategyDesc;
    private String createTime;
    private String updateTime;
}
