package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RaffleActivitySkuDao {
    void insert(RaffleActivitySku raffleActivitySku);

    RaffleActivitySku conditionQueryRaffleActivitySku(RaffleActivitySku raffleActivitySku);

    RaffleActivitySku queryActivitySku(@Param("sku") Long sku);

    void updateSkuStock(@Param("sku") Long sku,@Param("activityId") Long activityId);

    void updateSkuStockZero(@Param("sku") Long sku);

    List<RaffleActivitySku> queryActivitySkuByActivityId(@Param("activityId") Long activityId);

    List<RaffleActivitySku> queryActivitySkuList();
}
