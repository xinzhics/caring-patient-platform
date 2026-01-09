package com.caring.sass.common.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 患者更换医生
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisMessagePatientChangeDoctor {

    /**
     * 患者ID
     */
    public Long patientId;

    /**
     * 患者的IM账号
     */
    public String patientImAccount;

    /**
     * 旧的医生ID
     */
    private Long oldDoctorId;
    /**
     * 旧的医助ID
     */
    private Long oldNursingId;

    /**
     * 目标医生
     */
    public Long targetDoctorId;

    public String targetDoctorName;


    /**
     * 更换医助。
     */
    private Boolean changeNursing;

    /**
     * 目标医助
     */
    private Long targetNursingId;

    private String targetNursingName;


    private Long orgId;

    private String orgCode;

    private String orgName;

    private String classCode;

    private String tenantCode;


}
