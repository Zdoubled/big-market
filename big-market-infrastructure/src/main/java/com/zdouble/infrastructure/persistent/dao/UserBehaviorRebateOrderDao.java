package com.zdouble.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.zdouble.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.zdouble.infrastructure.persistent.po.UserBehaviorRebateOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserBehaviorRebateOrderDao {
    void insertUserBehaviorRebateOrder(UserBehaviorRebateOrder userBehaviorRebateOrder);
    @DBRouter(key = "userId")
    List<UserBehaviorRebateOrder> queryUserBehaviorRebateOrder(UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity);
}
