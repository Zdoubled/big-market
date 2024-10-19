package com.zdouble.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.zdouble.infrastructure.persistent.po.UserRaffleOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserRaffleOrderDao {
    @DBRouter(key = "userId")
    UserRaffleOrder queryUserRaffleOrder(UserRaffleOrder userRaffleOrder);

    void insert(UserRaffleOrder userRaffleOrder);

    int updateUserRaffleOrderStateUsed(UserRaffleOrder userRaffleOrder);
}
