package com.zdouble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDrawResponseDto {
    private Integer awardId;
    private String orderId;
    private String awardConfig;
    private Integer awardIndex;
    private String awardTitle;
}
