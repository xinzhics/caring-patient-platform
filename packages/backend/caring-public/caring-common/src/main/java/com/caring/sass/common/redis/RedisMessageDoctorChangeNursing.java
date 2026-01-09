package com.caring.sass.common.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医生更换医助
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisMessageDoctorChangeNursing {


    /**
     * 医生ID
     */
    public Long doctorId;

    /**
     * 目标医助
     */
    private Long targetNursingId;

    private String targetNursingName;

    /**
     * 原先的医助
     */
    private Long primaryNursingId;

    private Long orgId;

    private String orgCode;

    private String orgName;

    private String classCode;

    private String tenantCode;

}
