package com.zdouble.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAccountDayEntity {
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String userId;
    private Long activityId;
    private String day;
    private Integer dayCount;
    private Integer dayCountSurplus;

    public String getDay() {
        return sdf.format(new Date());
    }
}
