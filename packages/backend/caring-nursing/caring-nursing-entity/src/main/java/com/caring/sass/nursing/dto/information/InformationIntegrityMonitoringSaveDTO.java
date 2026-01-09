package com.caring.sass.nursing.dto.information;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 信息完整度监控指标
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "InformationIntegrityMonitoringSaveDTO", description = "信息完整度监控指标")
public class InformationIntegrityMonitoringSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "信息完整度监控指标ID 更新时候传入")
    private Long id;
    /**
     * 基本信息，疾病信息， 复查提醒，健康日志，护理计划，监测计划，自定义护理计划，自定义监测计划
     */
    @ApiModelProperty(value = "基本信息，疾病信息， 复查提醒，健康日志，护理计划，监测计划，自定义护理计划，自定义监测计划",required = true)
    @Length(max = 255, message = "基本信息，疾病信息， 复查提醒，健康日志，护理计划，监测计划，自定义护理计划，自定义监测计划长度不能超过255")
    private String businessType;
    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    private Long planId;

    @ApiModelProperty(value = "计划名称")
    private String planName;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID",required = true)
    private Long formId;
    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段ID",required = true)
    @Length(max = 40, message = "表单中字段ID长度不能超过40")
    private String fieldId;

    @ApiModelProperty(value = "表单中字段Label",required = true)
    private String fieldLabel;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序",required = true)
    private Integer monitorSort;
    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码",required = true)
    private String tenantCode;

}
