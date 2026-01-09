package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 表单结果导出记录表
 * </p>
 *
 * @author yangshuai
 * @since 2022-07-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FormResultExportPageDTO", description = "表单结果导出记录表")
public class FormResultExportPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 导出的文件名字
     */
    @ApiModelProperty(value = "导出的文件名字")
    @Length(max = 500, message = "导出的文件名字长度不能超过500")
    private String exportFileName;
    /**
     * 导出类型(baseInfo 基本信息 ,follow_up 随访计划)
     */
    @ApiModelProperty(value = "导出类型(baseInfo 基本信息 ,follow_up 随访计划)")
    @Length(max = 255, message = "导出类型(baseInfo 基本信息 ,follow_up 随访计划)长度不能超过255")
    private String exportType;
    /**
     * 导出的进度(已导出患者数/患者总数)
     */
    @ApiModelProperty(value = "导出的进度(已导出患者数/患者总数)")
    private Integer exportProgress;

}
