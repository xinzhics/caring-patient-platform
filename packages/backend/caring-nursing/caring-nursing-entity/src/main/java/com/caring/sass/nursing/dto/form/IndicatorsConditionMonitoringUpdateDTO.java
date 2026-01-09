package com.caring.sass.nursing.dto.form;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 患者管理-监测数据-监控指标条件表
 * </p>
 *
 * @author yangshuai
 * @since 2022-06-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "IndicatorsConditionMonitoringUpdateDTO", description = "患者管理-监测数据-监控指标条件表")
public class IndicatorsConditionMonitoringUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    private Long planId;
    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称")
    @Length(max = 200, message = "计划名称长度不能超过200")
    private String planName;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    private Long formId;
    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段ID")
    @Length(max = 40, message = "表单中字段ID长度不能超过40")
    private String fieldId;
    /**
     * 表单字段
     */
    @ApiModelProperty(value = "表单字段")
    @Length(max = 300, message = "表单字段长度不能超过300")
    private String fieldLabel;
    /**
     * 最大值条件符号
     */
    @ApiModelProperty(value = "最大值条件符号  1-小于 2-小于等于 3-等于")
    @Length(max = 40, message = "最大值条件符号长度不能超过40")
    private String maxConditionSymbol;
    /**
     * 最大值条件值
     */
    @ApiModelProperty(value = "最大值条件值")
    @Length(max = 40, message = "最大值条件值长度不能超过40")
    private String maxConditionValue;
    /**
     * 最小值条件符号
     */
    @ApiModelProperty(value = "最小值条件符号")
    @Length(max = 40, message = "最小值条件符号长度不能超过40")
    private String minConditionSymbol;
    /**
     * 最小值条件值
     */
    @ApiModelProperty(value = "最小值条件值")
    @Length(max = 40, message = "最小值条件值长度不能超过40")
    private String minConditionValue;
}
