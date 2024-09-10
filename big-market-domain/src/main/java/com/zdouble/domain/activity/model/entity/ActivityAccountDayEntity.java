package com.zdouble.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAccountDayEntity {
    private String userId;
    private Long activityId;
    private String day;
    private Integer dayCount;
    private Integer dayCountSurplus;
}
