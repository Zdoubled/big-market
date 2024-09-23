package com.zdouble.trigger.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zdouble.domain.activity.model.entity.ActivitySkuChargeEntity;
import com.zdouble.domain.activity.service.quota.RaffleActivityAccountQuotaService;
import com.zdouble.domain.rebate.event.UserBehaviorRebateMessageEvent;
import com.zdouble.domain.rebate.model.vo.RebateTypeVO;
import com.zdouble.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class UserBehaviorRebateOrderListener {

    @Resource
    private RaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.rebate_send}"))
    public void onMessage(String message) {
        try {
            BaseEvent.EventMessage<UserBehaviorRebateMessageEvent.SendUserBehaviorRebateMessage> event = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<UserBehaviorRebateMessageEvent.SendUserBehaviorRebateMessage>>() {
            }.getType());
            UserBehaviorRebateMessageEvent.SendUserBehaviorRebateMessage eventData = event.getData();
            // 处理库存返利
            if (eventData.getRebateType().getCode().equals(RebateTypeVO.sku.getCode())) {
                Long sku = Long.valueOf(eventData.getRebateConfig());
                String orderId = raffleActivityAccountQuotaService.createSkuRechargeOrder(ActivitySkuChargeEntity.builder()
                        .userId(eventData.getUserId())
                        .outBusinessNo(RandomStringUtils.randomNumeric(12))
                        .sku(sku)
                        .build()
                );
            }
        } catch (Exception e) {
            log.error("返利下发异常", e);
        }
    }
}
