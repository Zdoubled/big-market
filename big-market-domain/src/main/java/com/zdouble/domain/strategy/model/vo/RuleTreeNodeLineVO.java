package com.zdouble.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleTreeNodeLineVO {
    private String treeId;//树id
    private String treeNodeIdFrom;//"从"节点
    private String treeNodeIdTo;//"到"节点
    private RuleLimitTypeVO ruleLimitType;//1:= 2:> 3:< 4:>= 5:<= 6:枚举
    private RuleLogicCheckTypeVO ruleLimitValue;//规则限制值
}
