package com.caring.sass.common.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医助转移数据到其他医助
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisMessageNursingChange {

    public String tenantCode;
    /**
     * 医生ID
     */
    public Long nursingId;

    /**
     * 目标医助
     */
    private Long targetNursingId;

    private String targetNursingName;


    private Long orgId;

    private String orgCode;

    private String orgName;

    private String classCode;



}
