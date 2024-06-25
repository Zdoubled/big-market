package com.zdouble.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleActionEntity <T extends RaffleActionEntity.RaffleEntity> {

    private String code;
    private String info;
    private T data;
    private String ruleModel;

    public abstract static class RaffleEntity {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RaffleBeforeAction extends RaffleEntity {
        private Long strategyId;
        private String ruleWeightValue;
        private Integer awardId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RaffleCenterAction extends RaffleEntity {
        private Long strategyId;
        private String ruleLockValue;
        private Integer awardId;
    }

    public static class RaffleAfterAction extends RaffleEntity {

    }
}
