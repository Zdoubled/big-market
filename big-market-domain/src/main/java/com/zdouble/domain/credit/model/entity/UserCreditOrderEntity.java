package com.zdouble.domain.credit.model.entity;

import com.zdouble.domain.credit.model.vo.TradeNameVO;
import com.zdouble.domain.credit.model.vo.TradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditOrderEntity {
    private String userId;
    private String orderId;
    private TradeNameVO tradeName;
    private TradeTypeVO tradeType;
    private BigDecimal tradeAmount;
    private String outBusinessNo;
}
