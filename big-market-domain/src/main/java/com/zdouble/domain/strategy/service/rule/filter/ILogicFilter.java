package com.zdouble.domain.strategy.service.rule.filter;

import com.zdouble.domain.strategy.model.entity.RaffleActionEntity;
import com.zdouble.domain.strategy.model.entity.RaffleMatterEntity;

public interface ILogicFilter<T extends RaffleActionEntity.RaffleEntity> {


    RaffleActionEntity<T> doFilter(RaffleMatterEntity entity);
}
