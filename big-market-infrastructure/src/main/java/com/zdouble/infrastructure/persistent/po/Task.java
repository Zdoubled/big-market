package com.zdouble.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class Task {
    private Integer id;
    private String topic;
    private String message;
    private String state;
    private Date create_time;
    private Date update_time;
}
