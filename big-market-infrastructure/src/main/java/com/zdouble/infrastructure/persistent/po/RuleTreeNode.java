package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RuleTreeNode {
    private String treeId;
    private String ruleKey;
    private String ruleDesc;
    private String ruleValue;
    private Date createTime;
    private Date updateTime;
}
