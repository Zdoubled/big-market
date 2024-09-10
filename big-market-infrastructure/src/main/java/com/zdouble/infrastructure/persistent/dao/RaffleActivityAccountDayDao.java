package com.zdouble.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.zdouble.infrastructure.persistent.po.RaffleActivityAccountDay;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RaffleActivityAccountDayDao {
    List<RaffleActivityAccountDay> queryRaffleActivityAccountDayList();

    @DBRouter(key = "userId")
    RaffleActivityAccountDay queryActivityAccountDay(RaffleActivityAccountDay activityAccountDay);

    void insertActivityAccountAccountDay(RaffleActivityAccountDay raffleActivityAccountDay);

    int updateActivityAccountAccountDaySubtractionQuota(RaffleActivityAccountDay raffleActivityAccountDay);
}