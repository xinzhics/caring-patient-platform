package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用药预警-余药不足（购药余期）
 *
 * @author 代嘉乐
 */
@Data
@ApiModel(value = "DrugsDeficiencyVo", description = "用药预警-余药不足")
public class DrugsAvailableVo implements Serializable {
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
     * 患者药品具体信息
     */
    @ApiModelProperty(value = "患者药品具体信息")
    private List<DrugsAvailableMessageData> drugsDeficiencyMessageData;
}
