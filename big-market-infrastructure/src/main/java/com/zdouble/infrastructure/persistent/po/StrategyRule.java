package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

@Data
public class StrategyRule {
    private Long id;
    private Long strategyId;
    private Long awardId;
    private Short ruleType;
    private String ruleModel;
    private String ruleValue;
    private String ruleDesc;
    private String createTime;
    private String updateTime;
}
