package com.zdouble.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zdouble.types.common.Constants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyRuleEntity {
    private Long strategyId;
    private Integer awardId;
    private Short ruleType;
    private String ruleDesc;
    private String ruleModel;
    private String ruleValue;

    public Map<String , List<Integer>> getRuleWeightValues() {
        if (!ruleModel.equals("rule_weight")) {
            return null;
        }
        Map<String, List<Integer>> ruleWeightMap = new HashMap<>();
        String[] ruleWeightGroups = ruleValue.split(SPACE);
        for (String ruleWeightGroup : ruleWeightGroups) {
            String[] ruleValues = ruleWeightGroup.split(COLON);
            if (ruleValues.length < 2) {
                throw new IllegalArgumentException("rule value format error");
            }
            String[] ruleValue = ruleValues[1].split(SPLIT);
            ArrayList<Integer> ruleWeightValues = new ArrayList<>();
            for (String s : ruleValue) {
                ruleWeightValues.add(Integer.parseInt(s));
            }
            ruleWeightMap.put(ruleWeightGroup, ruleWeightValues);
        }
        return ruleWeightMap;
    }
}
