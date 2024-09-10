package com.zdouble.domain.activity.model.aggregate;

import com.zdouble.domain.activity.model.entity.ActivityAccountDayEntity;
import com.zdouble.domain.activity.model.entity.ActivityAccountEntity;
import com.zdouble.domain.activity.model.entity.ActivityAccountMonthEntity;
import com.zdouble.domain.activity.model.entity.UserRaffleOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePartakeOrderAggregate {
    private String userId;
    private Long activityId;
    private ActivityAccountEntity activityAccountEntity;
    private Boolean existMonth;
    private ActivityAccountMonthEntity activityAccountMonthEntity;
    private Boolean existDay;
    private ActivityAccountDayEntity activityAccountDayEntity;
    private UserRaffleOrderEntity userRaffleOrderEntity;
}
