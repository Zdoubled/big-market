package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class DailyBehaviorRebate {
    /** 主键id */
    private Integer id;
    /** 行为类型 */
    private String behaviorType;
    /** 返利描述 */
    private String rebateDesc;
    /** 返利类型 */
    private String rebateType;
    /** 返利配置 */
    private String rebateConfig;
    /** 返利行为状态 */
    private String state;
    private Date createTime;
    private Date updateTime;
}
