package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用药预警-已处理
 *
 * @author 代嘉乐
 */
@Data
@ApiModel(value = "DrugsHandledVo", description = "用药预警-已处理")
public class DrugsHandledVo implements Serializable {
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Long patientId;

    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    private String patientName;

    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像")
    private String avatar;

    /**
     * 患者所属医生ID
     */
    @ApiModelProperty(value = "患者所属医生ID")
    private Long doctorId;

    /**
     * 患者所属医生名称
     */
    @ApiModelProperty(value = "患者所属医生名称")
    private String doctorName;

    /**
     * 预警类型 (1 余药不足， 2 购药逾期)
     */
    @ApiModelProperty(value = "预警类型 (1 余药不足， 2 购药逾期)")
    private Integer warningType;

    /**
     * 异常处理时间
     */
    @ApiModelProperty(value = "异常处理时间")
    private LocalDateTime handleTime;
}
