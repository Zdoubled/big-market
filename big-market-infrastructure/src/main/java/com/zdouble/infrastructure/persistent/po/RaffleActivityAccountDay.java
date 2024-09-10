package com.zdouble.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleActivityAccountDay {
    private Integer id;
    private String userId;
    private Long activityId;
    private String day;
    private Integer dayCount;
    private Integer dayCountSurplus;
    private Date createTime;
    private Date updateTime;
}