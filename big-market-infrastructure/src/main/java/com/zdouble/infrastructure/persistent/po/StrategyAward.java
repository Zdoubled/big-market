package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StrategyAward {
    private Long id;
    private Long strategyId;
    private Long awardId;
    private String awardTitle;
    private String awardSubTitle;
    private Integer awardCount;
    private Integer awardCountSurplus;
    private BigDecimal awardRate;
    private String ruleModels;
    private Integer sort;
    private String createTime;
    private String updateTime;
}
