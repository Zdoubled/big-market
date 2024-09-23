package com.zdouble.domain.rebate.model.entity;

import com.zdouble.domain.rebate.model.vo.BehaviorTypeVO;
import com.zdouble.domain.rebate.model.vo.RebateTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBehaviorRebateOrderEntity {
    /** 用户id */
    private String userId;
    /** 订单id */
    private String orderId;
    /** 行为类型 */
    private BehaviorTypeVO behaviorType;
    /** 返利描述 */
    private String rebateDesc;
    /** 返利类型 */
    private RebateTypeVO rebateType;
    /** 返利配置 */
    private String rebateConfig;
    /** 唯一id */
    private String bizId;
}
