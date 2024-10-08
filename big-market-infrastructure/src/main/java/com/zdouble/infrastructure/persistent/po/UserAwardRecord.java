package com.zdouble.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAwardRecord {
    private Integer id;
    private String userId;
    private Long activityId;
    private Long strategyId;
    private String orderId;
    private Integer awardId;
    private String awardTitle;
    private Date awardTime;
    private String awardState;
    private Date createTime;
    private Date updateTime;
}
