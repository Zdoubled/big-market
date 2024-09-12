package com.zdouble.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Integer id;
    private String userId;
    private String topic;
    private String messageId;
    private String message;
    private String state;
    private Date createTime;
    private Date updateTime;
}
