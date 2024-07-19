package com.zdouble.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleTreeVO {
    private String treeId;//规则树名
    private String treeName;//规则树名
    private String treeDesc;//规则树描述
    private String ruleTreeRootNode;//规则根节点
    private Map<String, RuleTreeNodeVO> treeNodeMap;//规则节点
}
