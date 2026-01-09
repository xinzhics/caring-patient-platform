package com.caring.sass.nursing.dto.traceInto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @className: TraceFormPatientResultPageDTO
 * @author: 杨帅
 * 医助端 未处理 和已处理列表的模板
 * @date: 2023/8/10
 */
@Data
public class TraceFormPatientResultPageDTO {


    @ApiModelProperty("患者ID")
    private Long patientId;

    @ApiModelProperty("患者头像")
    private String patientAvatar;

    @ApiModelProperty("患者名称")
    private String patientName;

    @ApiModelProperty("患者IM账号")
    private String patientImAccount;

    @ApiModelProperty("医生ID")
    private Long doctorId;

    @ApiModelProperty("医生头像")
    private String doctorName;

    @ApiModelProperty("上传时间")
    private LocalDateTime pushTime;

    @ApiModelProperty("处理时间")
    private LocalDateTime handleTime;

    @ApiModelProperty("表单结果创建时间和字段选项标识")
    List<PlanFormDTO> formResultDto;

}
