package com.zdouble.domain.award.service;

import com.zdouble.domain.award.aggregrate.UserAwardRecordAggregate;
import com.zdouble.domain.award.event.UserAwardSendMessageEvent;
import com.zdouble.domain.award.model.entity.DistributeAwardEntity;
import com.zdouble.domain.award.model.entity.TaskEntity;
import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;
import com.zdouble.domain.award.model.vo.TaskStateVO;
import com.zdouble.domain.award.reporsitory.IAwardRepository;
import com.zdouble.domain.award.service.distribute.IDistributeAward;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.event.BaseEvent;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
public class AwardService implements IAwardService {

    @Resource
    private IAwardRepository awardRepository;
    @Resource
    private UserAwardSendMessageEvent userAwardSendMessageEvent;
    @Resource
    private Map<String, IDistributeAward> distributeAwardMap;

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        String userId = userAwardRecordEntity.getUserId();
        Integer awardId = userAwardRecordEntity.getAwardId();
        String awardTitle = userAwardRecordEntity.getAwardTitle();
        String awardConfig = userAwardRecordEntity.getAwardConfig();

        // 1. 构建消息发送对象
        UserAwardSendMessageEvent.SendAwardMessage sendAwardMessage = UserAwardSendMessageEvent.SendAwardMessage.builder()
                .awardId(awardId)
                .awardConfig(awardConfig)
                .orderId(userAwardRecordEntity.getOrderId())
                .awardTitle(awardTitle)
                .userId(userId)
                .build();
        BaseEvent.EventMessage<UserAwardSendMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = userAwardSendMessageEvent.buildEventMessage(sendAwardMessage);
        // 2. 构建task对象
        TaskEntity taskEntity = TaskEntity.builder()
                .state(TaskStateVO.create)
                .message(sendAwardMessageEventMessage)
                .topic(userAwardSendMessageEvent.topic())
                .userId(userId)
                .messageId(sendAwardMessageEventMessage.getId())
                .build();
        // 3. 构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .taskEntity(taskEntity)
                .userAwardRecordEntity(userAwardRecordEntity)
                .build();
        // 4. 落库
        awardRepository.insertAwardRecordAndTask(userAwardRecordAggregate);
    }

    @Override
    public void giveOutPrizes(DistributeAwardEntity distributeAwardEntity) {
        // 根据id奖品的award_key
        String awardKey = awardRepository.queryAwardKey(distributeAwardEntity.getAwardId());
        if (null == awardKey) {
            log.error("未找到奖品id不存在");
            return;
        }
        IDistributeAward distributeAward = distributeAwardMap.get(awardKey);
        if (distributeAward == null) {
            throw new RuntimeException("未找到奖品发放配置服务");
        }
        distributeAward.giveOutPrizes(distributeAwardEntity);
    }
}
