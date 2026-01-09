package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "MonitorProcessedVo", description = "监测数据已处理列表Vo对象")
public class MonitorProcessedVo implements Serializable {
    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    private  Long patientId;
    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    private String patientName;
    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间")
    private LocalDateTime handleTime;
    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像")
    private String patientAvatar;
    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Long doctorId;
    /**
     * 患者所属医生
     */
    @ApiModelProperty(value = "患者所属医生")
    private String doctorName;


}
