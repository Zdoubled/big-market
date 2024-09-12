package com.zdouble.domain.award.service;

import com.zdouble.domain.award.aggregrate.UserAwardRecordAggregate;
import com.zdouble.domain.award.event.UserAwardSendMessageEvent;
import com.zdouble.domain.award.model.entity.TaskEntity;
import com.zdouble.domain.award.model.entity.UserAwardRecordEntity;
import com.zdouble.domain.award.model.vo.TaskStateVO;
import com.zdouble.domain.award.reporsitory.IAwardRecordRepository;
import com.zdouble.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AwardService implements IAwardService {

    @Resource
    private IAwardRecordRepository awardRecordRepository;
    @Resource
    private UserAwardSendMessageEvent userAwardSendMessageEvent;

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        String userId = userAwardRecordEntity.getUserId();
        Integer awardId = userAwardRecordEntity.getAwardId();
        String awardTitle = userAwardRecordEntity.getAwardTitle();

        // 1. 构建消息发送对象
        UserAwardSendMessageEvent.SendAwardMessage sendAwardMessage = UserAwardSendMessageEvent.SendAwardMessage.builder()
                .awardId(awardId)
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
        awardRecordRepository.insertAwardRecordAndTask(userAwardRecordAggregate);
    }
}
