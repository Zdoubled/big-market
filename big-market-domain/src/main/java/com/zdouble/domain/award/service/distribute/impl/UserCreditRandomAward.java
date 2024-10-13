package com.zdouble.domain.award.service.distribute.impl;

import com.zdouble.domain.award.aggregrate.GiveOutPrizesAggregate;
import com.zdouble.domain.award.model.entity.DistributeAwardEntity;
import com.zdouble.domain.award.model.entity.UserAwardCreditEntity;
import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;
import com.zdouble.domain.award.model.vo.AwardStateVO;
import com.zdouble.domain.award.reporsitory.IAwardRepository;
import com.zdouble.domain.award.service.distribute.IDistributeAward;
import com.zdouble.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;

@Component("user_credit_random")
@Slf4j
public class UserCreditRandomAward implements IDistributeAward {

    @Resource
    private IAwardRepository awardRecordRepository;

    @Override
    public void giveOutPrizes(DistributeAwardEntity distributeAwardEntity) {
        log.info("用户积分随机奖励下发,奖品id：{}, 奖品配置：{}", distributeAwardEntity.getAwardId(), distributeAwardEntity.getAwardConfig());
        // 奖品id
        Integer awardId = distributeAwardEntity.getAwardId();
        // 奖品配置
        String awardConfig = distributeAwardEntity.getAwardConfig();

        String[] creditRange = awardConfig.split(Constants.SPLIT);
        if (creditRange.length != 2) {
            throw new RuntimeException("award_config 「" + awardConfig + "」配置不是一个范围值，如 1,100");
        }
        // 根据范围生成随机积分值
        BigDecimal creditRandom = generateCreditRandom(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]));

        // 构建聚合对象
        // 中奖记录对象
        UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                .userId(distributeAwardEntity.getUserId())
                .awardId(awardId)
                .orderId(distributeAwardEntity.getOrderId())
                .awardState(AwardStateVO.complete)
                .build();

        // 奖品积分发放实体对象
        UserAwardCreditEntity userAwardCreditEntity = UserAwardCreditEntity.builder().userId(distributeAwardEntity.getUserId()).creditAward(creditRandom).build();
        // 聚合对象
        GiveOutPrizesAggregate giveOutPrizesAggregate = GiveOutPrizesAggregate.builder()
                .userId(distributeAwardEntity.getUserId())
                .userAwardRecordEntity(userAwardRecordEntity)
                .userAwardCreditEntity(userAwardCreditEntity)
                .build();

        awardRecordRepository.saveGiveOutPrizesAggregate(giveOutPrizesAggregate);
    }

    private BigDecimal generateCreditRandom(BigDecimal min, BigDecimal max) {
        BigDecimal creditRandom = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return creditRandom.round(new MathContext(3));
    }


}
