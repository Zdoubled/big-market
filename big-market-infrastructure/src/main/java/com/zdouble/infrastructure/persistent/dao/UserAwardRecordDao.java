package com.zdouble.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.zdouble.infrastructure.persistent.po.UserAwardRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserAwardRecordDao {
    void insertUserAwardRecord(UserAwardRecord userAwardRecord);

    void updateUserAwardRecordState(UserAwardRecord userAwardRecord);
}
