package com.zdouble.domain.activity.service.partake;

import com.zdouble.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.zdouble.domain.activity.model.entity.ActivityEntity;
import com.zdouble.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.zdouble.domain.activity.model.entity.UserRaffleOrderEntity;
import com.zdouble.domain.activity.model.pojo.ActivityStateVO;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.activity.service.IRaffleActivityPartakeService;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;

import java.util.Date;

/**
 * 执行抽奖，创建订单模板
 */
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {

    protected IActivityRepository activityRepository;

    public AbstractRaffleActivityPartake(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public UserRaffleOrderEntity createOrder(String userId, Long activityId) {
        return createOrder(PartakeRaffleActivityEntity.builder()
                .activityId(activityId)
                .userId(userId)
                .build());
    }

    @Override
    public UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        // 参数校验
        Long activityId = partakeRaffleActivityEntity.getActivityId();
        String userId = partakeRaffleActivityEntity.getUserId();
        Date currentDate = new Date();
        if (activityId == null || userId == null) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        // 1. 获取Activity,并进行活动信息校验
        ActivityEntity activity = activityRepository.queryActivityByActivityId(activityId);
         /*  状态校验  */
        String state = activity.getState();
        if (!ActivityStateVO.open.getCode().equals(state)) {
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        /*  日期校验  */
        Date beginDateTime = activity.getBeginDateTime();
        Date endDateTime = activity.getEndDateTime();
        if (currentDate.before(beginDateTime) || currentDate.after(endDateTime)) {
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }
        // 2. 查看未被使用的活动参与订单记录，存在则直接返回
        UserRaffleOrderEntity userRaffleOrderEntity = activityRepository.queryUserRaffleOrder(activityId, userId);
        if (null != userRaffleOrderEntity) {
            return userRaffleOrderEntity;
        }
        // 3. 额度账户过滤 & 返回账户构建对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate = this.doFilter(userId, activityId, currentDate);
        // 4. 创建订单对象
        UserRaffleOrderEntity userRaffleOrder = createUserRaffleOrder(userId, activityId, currentDate);

        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrder);
        activityRepository.saveCreatePartakeOrderAggregate(createPartakeOrderAggregate);
        return userRaffleOrder;
    }

    protected abstract CreatePartakeOrderAggregate doFilter(String userId, Long activityId, Date today);

    protected abstract UserRaffleOrderEntity createUserRaffleOrder(String userId, Long activityId, Date currentDate);
}
