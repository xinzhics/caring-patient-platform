package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "LaunchReferralDTO", description = "发起转诊请求参数")
public class LaunchReferralDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者id
     */
    @NotNull(message = "患者id不能为空")
    @ApiModelProperty(value = "患者id")
    private Long patientId;


    /**
     * 发起医生id
     */
    @NotNull(message = "发起医生id不能为空")
    @ApiModelProperty(value = "发起医生id")
    private Long launchDoctorId;

    /**
     * 接收医生id
     */
    @NotNull(message = "接收医生id不能为空")
    @ApiModelProperty(value = "接收医生id")
    private Long acceptDoctorId;

    /**
     * 转诊性质：0单次转诊、1长期转诊
     */
    @NotNull(message = "转诊性质不能为空")
    @ApiModelProperty(value = "转诊性质：0单次转诊、1长期转诊")
    private Integer referralCategory;
}
