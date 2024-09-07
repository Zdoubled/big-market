package com.zdouble.infrastructure.persistent.dao;

import com.zdouble.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RaffleActivitySkuDao {
    void insert(RaffleActivitySku raffleActivitySku);

    RaffleActivitySku conditionQueryRaffleActivitySku(RaffleActivitySku raffleActivitySku);

    RaffleActivitySku queryActivitySku(Long sku);
}
