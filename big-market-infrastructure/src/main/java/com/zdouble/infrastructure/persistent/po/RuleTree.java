package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RuleTree {
    private String treeId;
    private String treeName;
    private String treeDesc;
    private String treeNodeRuleKey;
    private Date createTime;
    private Date updateTime;
}
