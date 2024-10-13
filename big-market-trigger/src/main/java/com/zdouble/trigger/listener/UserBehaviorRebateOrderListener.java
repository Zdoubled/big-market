package com.zdouble.trigger.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zdouble.domain.activity.model.entity.ActivitySkuChargeEntity;
import com.zdouble.domain.activity.service.quota.RaffleActivityAccountQuotaService;
import com.zdouble.domain.credit.model.entity.UserCreditRechargeEntity;
import com.zdouble.domain.credit.service.ICreditService;
import com.zdouble.domain.rebate.event.UserBehaviorRebateMessageEvent;
import com.zdouble.domain.rebate.model.vo.RebateTypeVO;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.event.BaseEvent;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
@Slf4j
public class UserBehaviorRebateOrderListener {

    @Resource
    private RaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    @Resource
    private ICreditService creditService;
    @Value("${spring.rabbitmq.topic.rebate_send}")
    private String topic;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.rebate_send}"))
    public void onMessage(String message) {
        try {
            BaseEvent.EventMessage<UserBehaviorRebateMessageEvent.SendUserBehaviorRebateMessage> event = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<UserBehaviorRebateMessageEvent.SendUserBehaviorRebateMessage>>() {
            }.getType());
            UserBehaviorRebateMessageEvent.SendUserBehaviorRebateMessage eventData = event.getData();
            // 处理不同返利类型
            RebateTypeVO rebateType = eventData.getRebateType();
            switch (rebateType) {
                // 处理库存返利
                case sku:
                    Long sku = Long.valueOf(eventData.getRebateConfig());
                    String skuOrderId = raffleActivityAccountQuotaService.createSkuRechargeOrder(ActivitySkuChargeEntity.builder()
                            .userId(eventData.getUserId())
                            .outBusinessNo(RandomStringUtils.randomNumeric(12))
                            .sku(sku)
                            .build()
                    );
                    break;
                // 处理用户积分返利
                case integral:
                    BigDecimal creditAdd = new BigDecimal(eventData.getRebateConfig());
                    String integralOrderId = creditService.createCreditRechargeOrder(UserCreditRechargeEntity.builder()
                            .userId(eventData.getUserId())
                            .outBusinessNo(RandomStringUtils.randomNumeric(12))
                            .creditRecharge(creditAdd)
                            .build()
                    );
                    break;
            }
        }catch (AppException e){
            if (ResponseCode.INDEX_DUP.getCode().equals(e.getCode())) {
                log.warn("监听用户行为返利消息，消费重复 topic: {} message: {}", topic, message, e);
                return;
            }
            throw e;
        } catch (Exception e) {
            log.error("返利下发异常", e);
            throw e;
        }
    }
}
