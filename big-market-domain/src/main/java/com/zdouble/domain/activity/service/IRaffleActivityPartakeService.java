package com.zdouble.domain.activity.service;

import com.zdouble.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.zdouble.domain.activity.model.entity.UserRaffleOrderEntity;

public interface IRaffleActivityPartakeService {
    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);
}
