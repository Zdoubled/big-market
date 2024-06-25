package com.zdouble.domain.strategy.service;

import com.zdouble.domain.strategy.model.entity.RaffleAwardEntity;
import com.zdouble.domain.strategy.model.entity.RaffleFactorEntity;

public interface IRaffleStrategy {

    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactoryEntity);
}
