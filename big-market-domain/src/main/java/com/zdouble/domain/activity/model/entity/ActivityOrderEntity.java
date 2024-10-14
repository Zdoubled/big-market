package com.zdouble.domain.activity.model.entity;

import com.zdouble.domain.activity.model.pojo.OrderStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOrderEntity {
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
    private OrderStateVO state;
    private String outBusinessNo;
}
