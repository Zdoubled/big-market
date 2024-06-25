package com.zdouble.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyEntity {
    private Long strategyId;
    private String strategyDesc;
    private String ruleModel;

    public String[] getModel(){
        if (StringUtils.isBlank(ruleModel)) {
            return new String[0];
        }
        return ruleModel.split(",");
    }

    public String getRuleWeight(){
        String[] ruleModels = getModel();
        for (String ruleModel : ruleModels) {
            if ("rule_weight".equals(ruleModel)) {
                return ruleModel;
            }
        }
        return null;
    }
}
