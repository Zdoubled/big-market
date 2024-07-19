package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RuleTreeNodeLine {
    private String treeId;
    private String ruleNodeFrom;
    private String ruleLimitType;
    private String ruleNodeTo;
    private String ruleLimitValue;
    private Date createTime;
    private Date updateTime;
}
