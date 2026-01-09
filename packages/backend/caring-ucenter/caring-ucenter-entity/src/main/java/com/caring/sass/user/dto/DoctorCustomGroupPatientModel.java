package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 医生的自定义小组患者
 * </p>
 *
 * @author yangshuai
 * @since 2022-08-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DoctorCustomGroupPatientModel", description = "添加小组患者时的数据列表")
public class DoctorCustomGroupPatientModel implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "患者ID")
    private Long id;
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者名称")
    private String name;

    @ApiModelProperty(value = "IM账号")
    private String imAccount;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "首字母")
    private String nameFirstLetter;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "医生备注")
    private String doctorRemark;

    @ApiModelProperty(value = "医助备注")
    private String remark;

    @ApiModelProperty(value = "是否选中， true 选中， false 未选中")
    private Boolean checked;

    @ApiModelProperty(value = "诊断类型名称")
    private String diagnosisName;

}
