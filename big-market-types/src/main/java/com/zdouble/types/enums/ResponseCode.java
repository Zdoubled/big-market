package com.zdouble.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),
    INDEX_DUP("0003", "唯一索引冲突"),
    STRATEGY_RULE_WEIGHT_IS_NULL("ERR_BIZ_001", "业务异常，策略规则中 rule_weight 权重规则已适用但未配置"),
    UN_ASSEMBLED_STRATEGY_ARMORY("ERR_BIZ_002", "凑将策略配置未装配，请通过IStrategyArmory完成装配"),

    ACTIVITY_STATE_ERROR("ERR_BIZ_003", "活动状态异常"),
    ACTIVITY_DATE_ERROR("ERR_BIZ_004", "不在活动时间范围内"),
    ACTIVITY_SKU_STOCK_ERROR("ERR_BIZ_005", "活动sku库存异常"),

    ACCOUNT_QUOTA_ERROR("ERR_BIZ_006", "活动账户额度异常"),
    ACCOUNT_MONTH_QUOTA_ERROR("ERR_BIZ_006", "活动月账户额度异常"),
    ACCOUNT_DAY_QUOTA_ERROR("ERR_BIZ_006", "活动日账户额度异常"),

    USER_ORDER_INSERT_ERROR("ERR_BIZ_007", "用户订单插入异常"),
    ;

    private String code;
    private String info;

}
