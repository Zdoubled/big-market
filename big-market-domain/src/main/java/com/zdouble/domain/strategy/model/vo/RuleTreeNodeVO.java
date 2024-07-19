package com.zdouble.domain.strategy.model.vo;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleTreeNodeVO {
    private String treeId;//树id
    private String ruleKey;//规则key
    private String ruleDesc;//规则描述
    private String ruleValue;//规则比值
    private List<RuleTreeNodeLineVO> treeNodeLinkList;//规则判断集合
}
