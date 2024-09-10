package com.zdouble.domain.activity.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityAccountMonthEntity {
    private String userId;
    private Long activityId;
    private String month;
    private Integer monthCount;
    private Integer monthCountSurplus;
}
