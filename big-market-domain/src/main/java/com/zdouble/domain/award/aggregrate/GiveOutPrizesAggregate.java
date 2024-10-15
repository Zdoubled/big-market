package com.zdouble.domain.award.aggregrate;

import com.zdouble.domain.award.model.entity.UserAwardCreditEntity;
import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiveOutPrizesAggregate {
    private String userId;
    private UserAwardRecordEntity userAwardRecordEntity;
    private UserAwardCreditEntity userAwardCreditEntity;
}
