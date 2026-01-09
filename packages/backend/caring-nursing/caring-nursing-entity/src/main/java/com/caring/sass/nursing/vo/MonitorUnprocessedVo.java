package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "MonitorUnprocessedVo", description = "监测数据未处理列表Vo对象")
public class MonitorUnprocessedVo implements Serializable {
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
    /**
     * 表单实际填写值和表单提交时间
     */
    @ApiModelProperty(value = "表单实际填写值和表单提交时间")
    private List<ValueAndTimeVo>  valueAndTimeVo;

}
