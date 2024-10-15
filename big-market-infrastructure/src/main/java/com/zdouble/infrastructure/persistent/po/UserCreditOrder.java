package com.zdouble.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditOrder {
    private String id;
    private String userId;
    private String orderId;
    private String tradeName;
    private String tradeType;
    private BigDecimal tradeAmount;
    private String outBusinessNo;
    private Date createTime;
    private Date updateTime;
}
