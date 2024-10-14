package com.zdouble.domain.credit.service;

import com.zdouble.domain.activity.event.CreditAdjustSuccessMessageEvent;
import com.zdouble.domain.award.model.vo.UserCreditAccountStatusVO;
import com.zdouble.domain.credit.aggregate.TradeAggregate;
import com.zdouble.domain.credit.model.entity.TaskEntity;
import com.zdouble.domain.credit.model.entity.TradeEntity;
import com.zdouble.domain.credit.model.entity.UserCreditAccountEntity;
import com.zdouble.domain.credit.model.entity.UserCreditOrderEntity;
import com.zdouble.domain.credit.model.vo.TaskStateVO;
import com.zdouble.domain.credit.repository.ICreditRepository;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.event.BaseEvent;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
@Slf4j
public class CreditService implements ICreditService {

    @Resource
    private ICreditRepository creditRepository;
    @Resource
    private CreditAdjustSuccessMessageEvent creditAdjustSuccessMessageEvent;

    @Value("${spring.rabbitmq.topic.credit_adjust_success}")
    private String topic;

    @Override
    public String createCreditAdjustOrder(TradeEntity tradeEntity) {
        // 参数校验
        String userId = tradeEntity.getUserId();
        BigDecimal tradeAmount = tradeEntity.getTradeAmount();
        String outBusinessNo = tradeEntity.getOutBusinessNo();
        if (StringUtils.isBlank(userId) || tradeAmount == null || StringUtils.isBlank(outBusinessNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        // 创建userCreditAccountEntity
        UserCreditAccountEntity userCreditAccountEntity = UserCreditAccountEntity.builder()
                .userId(userId)
                .totalAmount(tradeAmount)
                .availableAmount(tradeAmount)
                .accountStatus(UserCreditAccountStatusVO.open)
                .build();
        // 创建userCreditOrder
        UserCreditOrderEntity userCreditOrderEntity = UserCreditOrderEntity.builder()
                .userId(userId)
                .orderId(RandomStringUtils.randomNumeric(12))
                .tradeName(tradeEntity.getTradeName())
                .tradeType(tradeEntity.getTradeType())
                .tradeAmount(tradeAmount)
                .outBusinessNo(outBusinessNo)
                .build();
        // 构建task对象
        CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage creditAdjustSuccessMessage = CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage.builder()
                .userId(userCreditOrderEntity.getUserId())
                .orderId(userCreditOrderEntity.getOrderId())
                .amount(userCreditOrderEntity.getTradeAmount())
                .outBusinessNo(userCreditOrderEntity.getOutBusinessNo())
                .build();
        BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> eventMessage = creditAdjustSuccessMessageEvent.buildEventMessage(creditAdjustSuccessMessage);

        TaskEntity taskEntity = TaskEntity.builder()
                .userId(tradeEntity.getUserId())
                .topic(topic)
                .messageId(RandomStringUtils.randomNumeric(11))
                .message(eventMessage)
                .state(TaskStateVO.create)
                .build();

        // 创建聚合对象
        TradeAggregate tradeAggregate = TradeAggregate.builder()
                .userId(userId)
                .userCreditAccountEntity(userCreditAccountEntity)
                .userCreditOrderEntity(userCreditOrderEntity)
                .taskEntity(taskEntity)
                .build();
        // 保存
        creditRepository.doSaveUserCreditAdjust(tradeAggregate);

        // 保存返回订单id
        return userCreditOrderEntity.getOrderId();
    }
}
