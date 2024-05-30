package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

@Data
public class Award {
    private String id;
    private String awardId;
    private String awardKey;
    private String awardConfig;
    private String awardDesc;
    private String createTime;
    private String updateTime;
}
