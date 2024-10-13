package com.zdouble.domain.award.service;

import com.zdouble.domain.award.event.UserAwardSendMessageEvent;
import com.zdouble.domain.award.model.entity.DistributeAwardEntity;
import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;

/**
 * 抽奖相关服务
 */
public interface IAwardService {
    /**
     * 保存用户中奖记录
     *
     * @param userAwardRecordEntity
     */
    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

    /**
     * 奖品发放接口
     * @param distributeAwardEntity
     */
    void giveOutPrizes(DistributeAwardEntity distributeAwardEntity);
}
