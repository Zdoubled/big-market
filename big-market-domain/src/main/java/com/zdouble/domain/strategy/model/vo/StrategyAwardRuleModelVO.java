package com.zdouble.domain.strategy.model.vo;

import com.zdouble.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

import static com.zdouble.types.common.Constants.SPLIT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyAwardRuleModelVO {
    private String ruleModels;

    public String[] raffleCenterRuleModelList(){
        String[] ruleModelValues = ruleModels.split(SPLIT);
        ArrayList<String> ruleModelList = new ArrayList<>();
        for (String ruleModelValue : ruleModelValues) {
            if (DefaultLogicFactory.LogicModel.isCenter(ruleModelValue)){
                ruleModelList.add(ruleModelValue);
            }
        }

        return ruleModelList.toArray(new String[0]);
    }
}
