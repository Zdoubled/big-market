package com.zdouble.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleWeightVO {

    private Integer wight;
    private List<Award> awardList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Award{
        private Integer awardId;
        private String awardTitle;
    }
}
