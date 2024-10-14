package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RaffleActivitySku {
    private Long id;
    private Long sku;
    private Long activityId;
    private Long activityCountId;
    private Integer stockCount;
    private Integer stockCountSurplus;
    private BigDecimal productAmount;
    private Date createTime;
    private Date updateTime;
}
