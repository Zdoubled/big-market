package com.zdouble.domain.award.reporsitory;


import com.zdouble.domain.award.aggregrate.GiveOutPrizesAggregate;
import com.zdouble.domain.award.aggregrate.UserAwardRecordAggregate;

public interface IAwardRepository {
    void insertAwardRecordAndTask(UserAwardRecordAggregate userAwardRecordAggregate);

    void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate);

    String queryAwardKey(Integer awardId);
}
