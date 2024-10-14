package com.zdouble.domain.activity.model.entity;

import com.zdouble.domain.activity.model.pojo.OrderTradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySkuChargeEntity {
    private Long sku;
    private String userId;
    private String outBusinessNo;
    private OrderTradeTypeVO orderTradeType;
}
