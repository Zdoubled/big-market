package com.zdouble.domain.activity.service.partake;

import com.zdouble.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.zdouble.domain.activity.model.entity.*;
import com.zdouble.domain.activity.model.pojo.OrderStateVO;
import com.zdouble.domain.activity.model.pojo.UserRaffleOrderStateVO;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class RaffleActivityPartakeService extends AbstractRaffleActivityPartake{
    private final SimpleDateFormat simpleDayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat simpleMonthDateFormat = new SimpleDateFormat("yyyy-MM");

    public RaffleActivityPartakeService(IActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    protected UserRaffleOrderEntity createUserRaffleOrder(String userId, Long activityId, Date currentDate) {
        ActivityEntity activity = activityRepository.queryActivityByActivityId(activityId);
        return UserRaffleOrderEntity.builder()
                .userId(userId)
                .orderId(RandomStringUtils.randomNumeric(12))
                .activityId(activityId)
                .activityName(activity.getActivityName())
                .strategyId(activity.getStrategyId())
                .orderState(UserRaffleOrderStateVO.create)
                .orderTime(currentDate)
                .endTime(activity.getEndDateTime())
                .build();
    }

    @Override
    protected CreatePartakeOrderAggregate doFilter(String userId, Long activityId, Date currentDate) {
        /** 此处存在并发问题，高并发情况下会导致额度扣减到负数, 已结合reids解决*/
        // 1. 查看总账户额度
        ActivityAccountEntity activityAccountEntity = activityRepository.queryActivityAccount(userId, activityId);
        if (null == activityAccountEntity || activityAccountEntity.getTotalCountSurplus() <= 0) {
            log.info("用户[{}]没有足够的额度，无法参与活动[{}]", userId, activityId);
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }
        String month = simpleMonthDateFormat.format(currentDate);
        ActivityAccountMonthEntity activityAccountMonth = activityRepository.queryActivityAccountMonth(userId, activityId, month);
        boolean existMonth = null != activityAccountMonth;
        // 不存在，创建月额度对象
        if (!existMonth){
            activityAccountMonth = ActivityAccountMonthEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .monthCount(activityAccountEntity.getMonthCount())
                    .monthCountSurplus(activityAccountEntity.getMonthCount())
                    .month(month)
                    .build();
        }

        String day = simpleDayDateFormat.format(currentDate);
        ActivityAccountDayEntity activityAccountDay = activityRepository.queryActivityAccountDay(userId, activityId, day);
        boolean existDay = null != activityAccountDay;
        // 不存在，创建日额度对象
        if (!existDay){
            activityAccountDay = ActivityAccountDayEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .dayCount(activityAccountEntity.getDayCount())
                    .dayCountSurplus(activityAccountEntity.getDayCount())
                    .day(day)
                    .build();
        }
        // 2. 创建账户构建对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate = new CreatePartakeOrderAggregate();

        createPartakeOrderAggregate.setActivityId(activityId);
        createPartakeOrderAggregate.setUserId(userId);
        createPartakeOrderAggregate.setExistDay(existDay);
        createPartakeOrderAggregate.setExistMonth(existMonth);
        createPartakeOrderAggregate.setActivityAccountEntity(activityAccountEntity);
        createPartakeOrderAggregate.setActivityAccountMonthEntity(activityAccountMonth);
        createPartakeOrderAggregate.setActivityAccountDayEntity(activityAccountDay);
        return createPartakeOrderAggregate;
    }
}
