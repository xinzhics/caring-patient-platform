package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 信息完整度历史详情Vo对象
 * @author Evan Zhai
 */
@Data
@ApiModel(value = "CompletenessInformationVo", description = "信息完整度历史详情Vo对象")
public class CompletenessInformationVo implements Serializable {

    private static final long serialVersionUID = 7845832681008465436L;
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Long patientId;

    /**
     * 最后填写时间
     */
    @ApiModelProperty(value = "最后填写时间")
    private LocalDateTime lastWriteTime;

    @ApiModelProperty(value = "患者名")
    private String patientName;

    @ApiModelProperty(value = "患者头像")
    private String avatar;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "患者所属医生")
    private String doctorName;
}
