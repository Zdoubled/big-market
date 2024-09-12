package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RaffleActivitySkuDao {
    void insert(RaffleActivitySku raffleActivitySku);

    RaffleActivitySku conditionQueryRaffleActivitySku(RaffleActivitySku raffleActivitySku);

    RaffleActivitySku queryActivitySku(Long sku);

    void updateSkuStock(Long sku, Long activityId);

    void updateSkuStockZero(Long sku);

    List<RaffleActivitySku> queryActivitySkuByActivityId(Long activityId);
}
