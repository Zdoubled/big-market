package com.zdouble.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.zdouble.infrastructure.persistent.po.RaffleActivityAccountMonth;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RaffleActivityAccountMonthDao {
    @DBRouter(key = "userId")
    RaffleActivityAccountMonth queryActivityAccountMonth(RaffleActivityAccountMonth activityAccountMonth);

    void insertActivityAccountAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonth);

    int updateActivityAccountAccountMonthSubtractionQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);
}
