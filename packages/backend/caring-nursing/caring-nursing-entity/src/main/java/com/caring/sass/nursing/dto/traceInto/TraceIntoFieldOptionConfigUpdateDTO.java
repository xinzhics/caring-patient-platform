package com.caring.sass.nursing.dto.traceInto;

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
 * 选项跟踪表单字段选项配置表
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TraceIntoFieldOptionConfigUpdateDTO", description = "选项跟踪表单字段选项配置表")
public class TraceIntoFieldOptionConfigUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 随访计划ID
     */
    @ApiModelProperty(value = "随访计划ID")
    @NotNull(message = "随访计划ID不能为空")
    private Long planId;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    @NotNull(message = "表单ID不能为空")
    private Long formId;
    /**
     * 父题目的ID
     */
    @ApiModelProperty(value = "父题目的ID")
    @Length(max = 100, message = "父题目的ID长度不能超过100")
    private String parentFieldId;
    /**
     * 父题目选项的ID
     */
    @ApiModelProperty(value = "父题目选项的ID")
    @Length(max = 100, message = "父题目选项的ID长度不能超过100")
    private String parentFieldOptionId;
    /**
     * 跟踪的选项它所属的题目
     */
    @ApiModelProperty(value = "跟踪的选项它所属的题目")
    @NotEmpty(message = "跟踪的选项它所属的题目不能为空")
    @Length(max = 100, message = "跟踪的选项它所属的题目长度不能超过100")
    private String formFieldId;
    /**
     * 最终跟踪的选项
     */
    @ApiModelProperty(value = "最终跟踪的选项")
    @NotEmpty(message = "最终跟踪的选项不能为空")
    @Length(max = 100, message = "最终跟踪的选项长度不能超过100")
    private String fieldOptionId;
    /**
     * 是否子题目（0 否 1是）
     */
    @ApiModelProperty(value = "是否子题目（0 否 1是）")
    @NotNull(message = "是否子题目（0 否 1是）不能为空")
    private Integer isChildField;
}
