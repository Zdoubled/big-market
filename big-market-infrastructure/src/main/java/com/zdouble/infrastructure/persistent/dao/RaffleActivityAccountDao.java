package com.zdouble.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.zdouble.infrastructure.persistent.po.RaffleActivityAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RaffleActivityAccountDao {
    void insert(RaffleActivityAccount raffleActivityAccount);

    int update(RaffleActivityAccount raffleActivityAccount);
}
