package com.zdouble.domain.activity.model.aggregate;

import com.zdouble.domain.activity.model.entity.ActivityOrderEntity;
import com.zdouble.domain.activity.model.pojo.OrderStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuotaOrderAggregate {
    private String userId;
    private Long activityId;
    private Integer totalCount;
    private Integer totalCountSurplus;
    private Integer dayCount;
    private Integer dayCountSurplus;
    private Integer monthCount;
    private Integer monthCountSurplus;
    private ActivityOrderEntity activityOrderEntity;

    public void setOrderStateVO(OrderStateVO orderStateVO) {
        activityOrderEntity.setState(orderStateVO);
    }
}
