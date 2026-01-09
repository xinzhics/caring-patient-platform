package com.caring.sass.nursing.dto.drugs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 患者管理-用药预警-预警条件表
 * </p>
 *
 * @author yangshuai
 * @since 2022-06-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DrugsConditionMonitoringSaveDTO", description = "患者管理-用药预警-预警条件表")
public class DrugsConditionMonitoringSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "患者管理-用药预警-预警条件表ID 更新时候传入")
    private Long id;
    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码",required = true)
    @NotBlank(message = "租户编码不能为空")
    private String tenantCode;

    /**
     * 药品ID
     */
    @ApiModelProperty(value = "药品ID",required = true)
    @NotNull(message = "药品ID不能为空！")
    private Long drugsId;
    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称",required = true)
    @Length(max = 200, message = "药品名称长度不能超过200")
    @NotBlank(message = "药品名称不能为空")
    private String drugsName;
    /**
     * 规格
     */
    @ApiModelProperty(value = "规格",required = true)
    @Length(max = 2000, message = "规格长度不能超过2000")
    @NotBlank(message = "规格不能为空")
    private String spec;
    /**
     * 厂商
     */
    @ApiModelProperty(value = "厂商",required = true)
    @Length(max = 100, message = "厂商长度不能超过100")
    @NotBlank(message = "厂商不能为空")
    private String manufactor;
    /**
     * 提醒时间（余药不足前X天）
     */
    @ApiModelProperty(value = "提醒时间（余药不足前X天）")
    private Long reminderTime;
    /**
     * 购药地址
     */
    @ApiModelProperty(value = "购药地址")
    @Length(max = 1000, message = "购药地址长度不能超过1000")
    private String buyingMedicineUrl;
    /**
     * 模板消息ID
     */
    @ApiModelProperty(value = "模板消息ID")
    private Long templateMsgId;

}
