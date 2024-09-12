package com.zdouble.domain.award.reporsitory;


import com.zdouble.domain.award.aggregrate.UserAwardRecordAggregate;
import com.zdouble.domain.award.model.entity.TaskEntity;

import java.util.List;

public interface IAwardRecordRepository {
    void insertAwardRecordAndTask(UserAwardRecordAggregate userAwardRecordAggregate);
}
