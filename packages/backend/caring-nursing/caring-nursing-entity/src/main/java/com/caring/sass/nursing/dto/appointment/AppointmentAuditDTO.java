package com.caring.sass.nursing.dto.appointment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @className: AppointmentAuditDTO
 * @author: 杨帅
 * @date: 2023/3/29
 * 医助医生预约审核时表单
 */
@Data
public class AppointmentAuditDTO {

    @ApiModelProperty(value = "预约记录ID")
    @NotNull
    private Long appointmentId;

    @ApiModelProperty(value = "预约状态  通过：0  拒绝 3 ")
    @NotNull
    private Integer status;

    @ApiModelProperty(value = "ABOUT_FULL: 该时段已约满， OTHER: 其他")
    private String auditFailReason;

    /**
     * 审核拒绝原因说明
     */
    @ApiModelProperty(value = "审核拒绝原因说明")
    private String auditFailReasonDesc;
}
