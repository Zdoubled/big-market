package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RaffleActivityOrder {
    private Long id;
    private String userId;
    private Long sku;
    private Long activityId;
    private String activityName;
    private Long strategyId;
    private String orderId;
    private Date orderTime;
    private Integer totalCount;
    private Integer dayCount;
    private Integer monthCount;
    private BigDecimal payAmount;
    private String state;
    private Date createTime;
    private Date updateTime;
    private String outBusinessNo;
}
