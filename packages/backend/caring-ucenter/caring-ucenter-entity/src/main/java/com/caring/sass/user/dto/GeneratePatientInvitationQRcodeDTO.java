package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 实体类
 * 运营配置-患者推荐配置
 * </p>
 *
 * @author lixiang
 * @since 2022-07-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "GeneratePatientInvitationQRcodeDTO", description = "运营配置-推荐人生成邀请二维码")
public class GeneratePatientInvitationQRcodeDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "运营配置-患者推荐配置表ID")
    private Long id;
    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码",required = true)
    @NotBlank(message = "租户编码不能为空")
    private String tenantCode;

    /**
     * 二维码医生类型 (1 推荐人原医生， 2 指定医生)
     */
    @ApiModelProperty(value = "二维码医生类型 (1 推荐人原医生， 2 指定医生)")
    @NotNull(message = "二维码医生类型不能为空！")
    private Integer qrDoctorType;
    /**
     * 二维码医生ID
     */
    @ApiModelProperty(value = "二维码医生ID")
    private Long doctorId;
    /**
     * 二维码医生名称
     */
    @ApiModelProperty(value = "二维码医生名称")
    @Length(max = 40, message = "二维码医生名称长度不能超过40")
    private String doctorName;
    /**
     * 活动规则介绍开关 (1 关闭， 2 开启)
     */
    @ApiModelProperty(value = "活动规则介绍开关 (1 关闭， 2 开启)")
    @NotNull(message = "活动规则介绍开关不能为空！")
    private Integer activityRuleSwitch;
    /**
     * 活动规则介绍H5链接
     */
    @ApiModelProperty(value = "活动规则介绍H5链接")
    @Length(max = 1000, message = "活动规则介绍H5链接长度不能超过1000")
    private String activityRuleUrl;
    /**
     * 活动海报链接
     */
    @ApiModelProperty(value = "活动海报链接")
    @Length(max = 1000, message = "活动海报链接长度不能超过1000")
    @NotBlank(message = "活动海报链接不能为空")
    private String posterUrl;
    /**
     * 活动截止日期
     */
    @ApiModelProperty(value = "活动截止日期")
    private LocalDate activityEndDate;

    @ApiModelProperty(value = "二维码图像的高度", name = "height", dataType = "int")
    int height;

    @ApiModelProperty(value = "二维码图像的宽度", name = "width", dataType = "int")
    int width;

    @ApiModelProperty(value = "二维码图像的url地址", name = "url", dataType = "String", allowEmptyValue = false)
    String url;
}
