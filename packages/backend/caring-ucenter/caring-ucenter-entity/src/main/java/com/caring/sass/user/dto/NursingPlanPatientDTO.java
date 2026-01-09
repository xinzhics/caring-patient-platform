package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @ClassName NursingPlanPatientIds
 * @Description
 * @Author yangShuai
 * @Date 2021/9/1 14:03
 * @Version 1.0
 */
@Data
@ApiModel("护理计划传输请求用户信息使用")
public class NursingPlanPatientDTO {

    private List<Long> ids;

    private String tenantCode;

    public NursingPlanPatientDTO(List<Long> personIds, String tenantCode) {
        ids = personIds;
        this.tenantCode = tenantCode;
    }

    public NursingPlanPatientDTO() {
    }
}
