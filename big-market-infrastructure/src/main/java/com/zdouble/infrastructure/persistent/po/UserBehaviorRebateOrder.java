package com.zdouble.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBehaviorRebateOrder {
    /** 自增id */
    private Integer id;
    /** 用户id */
    private String userId;
    /** 订单id */
    private String orderId;
    /** 行为类型 */
    private String behaviorType;
    /** 返利描述 */
    private String rebateDesc;
    /** 返利类型 */
    private String rebateType;
    /** 返利配置 */
    private String rebateConfig;
    /** 唯一id */
    private String bizId;
    /** 业务单号 */
    private String outBusinessNo;
    private Date createTime;
    private Date updateTime;
}
