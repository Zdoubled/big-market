package com.zdouble.domain.rebate.service;

import com.zdouble.domain.rebate.event.UserBehaviorRebateMessageEvent;
import com.zdouble.domain.rebate.model.aggregate.UserBehaviorRebateAggregate;
import com.zdouble.domain.rebate.model.entity.DailyBehaviorRebateEntity;
import com.zdouble.domain.rebate.model.entity.TaskEntity;
import com.zdouble.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.zdouble.domain.rebate.model.vo.BehaviorTypeVO;
import com.zdouble.domain.rebate.model.vo.RebateTypeVO;
import com.zdouble.domain.rebate.model.vo.TaskStateVO;
import com.zdouble.domain.rebate.repository.IBehaviorRebateRepository;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.event.BaseEvent;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BehaviorRebateService extends AbstractBehaviorRebateService {

    public BehaviorRebateService(IBehaviorRebateRepository behaviorRebateRepository, UserBehaviorRebateMessageEvent userBehaviorRebateMessageEvent) {
        super(behaviorRebateRepository, userBehaviorRebateMessageEvent);
    }

    @Override
    protected List<UserBehaviorRebateOrderEntity> createUserBehaviorRebateOrder(String userId, List<DailyBehaviorRebateEntity> dailyBehaviorRebates) {
        if (null == dailyBehaviorRebates || dailyBehaviorRebates.isEmpty()) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        return dailyBehaviorRebates.stream().map(dailyBehaviorRebateEntity -> {
            return UserBehaviorRebateOrderEntity.builder()
                    .behaviorType(dailyBehaviorRebateEntity.getBehaviorType())
                    .outBusinessNo(dailyBehaviorRebateEntity.generateOutBusinessNo())
                    .bizId(dailyBehaviorRebateEntity.getBizId(userId))
                    .rebateConfig(dailyBehaviorRebateEntity.getRebateConfig())
                    .rebateType(dailyBehaviorRebateEntity.getRebateType())
                    .rebateDesc(dailyBehaviorRebateEntity.getRebateDesc())
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .userId(userId)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    protected List<UserBehaviorRebateAggregate> createUserBehaviorRebateAggregate(String userId, List<UserBehaviorRebateOrderEntity> userBehaviorRebateOrders) {
        return userBehaviorRebateOrders.stream().map(userBehaviorRebateOrder -> {
            // 1. 创建消息对象
            BaseEvent.EventMessage<UserBehaviorRebateMessageEvent.SendUserBehaviorRebateMessage> eventMessage = userBehaviorRebateMessageEvent.buildEventMessage(UserBehaviorRebateMessageEvent.SendUserBehaviorRebateMessage.builder()
                    .userId(userId)
                    .rebateType(userBehaviorRebateOrder.getRebateType())
                    .rebateConfig(userBehaviorRebateOrder.getRebateConfig())
                    .bizId(userBehaviorRebateOrder.getBizId())
                    .build()
            );
            // 2.. 创建task任务对象
            TaskEntity taskEntity = TaskEntity.builder()
                    .topic(userBehaviorRebateMessageEvent.topic())
                    .userId(userId)
                    .messageId(eventMessage.getId())
                    .message(eventMessage)
                    .state(TaskStateVO.create)
                    .build();
            // 3. 创建聚合对象
            return UserBehaviorRebateAggregate.builder()
                    .userId(userId)
                    .taskEntity(taskEntity)
                    .userBehaviorRebateOrder(userBehaviorRebateOrder)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserBehaviorRebateOrderEntity> isCalendarSignRebate(String userId, String outBusinessNo) {
        UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity = UserBehaviorRebateOrderEntity.builder()
                .userId(userId)
                .outBusinessNo(outBusinessNo)
                .behaviorType(BehaviorTypeVO.sign)
                .build();
        return behaviorRebateRepository.queryUserBehaviorRebateOrder(userBehaviorRebateOrderEntity);
    }
}
