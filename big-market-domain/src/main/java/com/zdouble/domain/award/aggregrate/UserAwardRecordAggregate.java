package com.zdouble.domain.award.aggregrate;

import com.zdouble.domain.award.model.entity.TaskEntity;
import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * 聚合对象
 */
public class UserAwardRecordAggregate {
    private UserAwardRecordEntity userAwardRecordEntity;
    private TaskEntity taskEntity;
}
