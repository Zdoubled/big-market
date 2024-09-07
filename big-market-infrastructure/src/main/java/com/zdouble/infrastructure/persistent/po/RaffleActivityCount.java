package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivityCount {
      private Long id;
      private Long activityCountId;
      private Integer totalCount;
      private Integer dayCount;
      private Integer monthCount;
      private Date createTime;
      private Date updateTime;
}
