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
public class TradeEntity {
    private String userId;
    private String outBusinessNo;
    private BigDecimal tradeAmount;
    private TradeTypeVO tradeType;
    private TradeNameVO tradeName;
}
