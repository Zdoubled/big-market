package com.zdouble.domain.activity.service.quota;

import com.zdouble.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.zdouble.domain.activity.model.entity.*;
import com.zdouble.domain.activity.repository.IActivityRepository;
import com.zdouble.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.zdouble.domain.activity.service.quota.policy.ITradePolicy;
import com.zdouble.domain.activity.service.quota.rule.IActionChain;
import com.zdouble.domain.activity.service.quota.rule.factory.DefaultActionChainFactory;
import com.zdouble.types.enums.ResponseCode;
import com.zdouble.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
public abstract class AbstractRaffleActivityAccountQuota extends RaffleActivitySupport implements IRaffleActivityAccountQuotaService {

    public AbstractRaffleActivityAccountQuota(IActivityRepository activityRepository, DefaultActionChainFactory defaultActionChainFactory, Map<String, ITradePolicy> tradePolicyMap) {
        super(activityRepository, defaultActionChainFactory, tradePolicyMap);
    }

    @Override
    public String createSkuRechargeOrder(ActivitySkuChargeEntity activitySkuChargeEntity) {
        // 1. 参数校验
        Long sku = activitySkuChargeEntity.getSku();
        String userId = activitySkuChargeEntity.getUserId();
        String outBusinessNo = activitySkuChargeEntity.getOutBusinessNo();
        if (null == sku || StringUtils.isBlank(userId) || StringUtils.isBlank(outBusinessNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        // 2. 查询数据库
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        ActivityEntity activityEntity = queryActivityByActivityId(activitySkuEntity.getActivityId());
        ActivityCountEntity activityCountEntity = queryActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        // 3. 活动规则过滤处理（活动状态、活动时间等）
        IActionChain actionChain = defaultActionChainFactory.openChain();
        actionChain.action(activitySkuEntity, activityEntity, activityCountEntity);
        // 4. 封装聚合对象  提取方法，交由子类实现抽象方法
        CreateQuotaOrderAggregate createQuotaOrderAggregate = buildOrderAggregate(activitySkuEntity, activityEntity, activityCountEntity, activitySkuChargeEntity);
        // 5. 查看交易是否消耗积分
        ITradePolicy tradePolicy = tradePolicyMap.get(activitySkuChargeEntity.getOrderTradeType().getCode());
        if (null == tradePolicy) {
            throw new AppException(ResponseCode.DATA_NOT_EXIST.getCode(), "活动交易规则不存在");
        }
        // 交易执行
        tradePolicy.trade(createQuotaOrderAggregate);
        // 6. 返回结果
        return createQuotaOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity, ActivitySkuChargeEntity activitySkuChargeEntity);
}
