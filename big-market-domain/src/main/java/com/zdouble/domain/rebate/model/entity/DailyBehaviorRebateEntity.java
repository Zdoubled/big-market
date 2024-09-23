package com.zdouble.domain.rebate.model.entity;

import com.zdouble.domain.rebate.model.vo.BehaviorRebateState;
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
public class DailyBehaviorRebateEntity {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    /** 行为类型 */
    private BehaviorTypeVO behaviorType;
    /** 返利描述 */
    private String rebateDesc;
    /** 返利类型 */
    private RebateTypeVO rebateType;
    /** 返利配置 */
    private String rebateConfig;
    /** 返利行为状态 */
    private BehaviorRebateState state;

    public String getBizId(String userId) {
        return userId + "_" + rebateType + "_" + sdf.format(new Date());
    }
}
