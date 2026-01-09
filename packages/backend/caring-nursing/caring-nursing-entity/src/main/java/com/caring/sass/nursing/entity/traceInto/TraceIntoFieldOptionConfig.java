package com.caring.sass.nursing.entity.traceInto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_trace_into_field_option_config")
@ApiModel(value = "TraceIntoFieldOptionConfig", description = "选项跟踪表单字段选项配置表")
@AllArgsConstructor
public class TraceIntoFieldOptionConfig extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 随访计划ID
     */
    @ApiModelProperty(value = "随访计划ID")
    @NotNull(message = "随访计划ID不能为空")
    @TableField("plan_id")
    @Excel(name = "随访计划ID")
    private Long planId;

    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    @NotNull(message = "表单ID不能为空")
    @TableField("form_id")
    @Excel(name = "表单ID")
    private Long formId;

    /**
     * 父题目的ID
     */
    @ApiModelProperty(value = "父题目的ID")
    @Length(max = 100, message = "父题目的ID长度不能超过100")
    @TableField(value = "parent_field_id", condition = LIKE)
    @Excel(name = "父题目的ID")
    private String parentFieldId;

    /**
     * 父题目选项的ID
     */
    @ApiModelProperty(value = "父题目选项的ID")
    @Length(max = 100, message = "父题目选项的ID长度不能超过100")
    @TableField(value = "parent_field_option_id", condition = LIKE)
    @Excel(name = "父题目选项的ID")
    private String parentFieldOptionId;

    /**
     * 跟踪的选项它所属的题目
     */
    @ApiModelProperty(value = "跟踪的选项它所属的题目")
    @NotEmpty(message = "跟踪的选项它所属的题目不能为空")
    @Length(max = 100, message = "跟踪的选项它所属的题目长度不能超过100")
    @TableField(value = "form_field_id", condition = LIKE)
    @Excel(name = "跟踪的选项它所属的题目")
    private String formFieldId;

    /**
     * 最终跟踪的选项
     */
    @ApiModelProperty(value = "最终跟踪的选项")
    @NotEmpty(message = "最终跟踪的选项不能为空")
    @Length(max = 100, message = "最终跟踪的选项长度不能超过100")
    @TableField(value = "field_option_id", condition = LIKE)
    @Excel(name = "最终跟踪的选项")
    private String fieldOptionId;

    /**
     * 是否子题目（0 否 1是）
     */
    @ApiModelProperty(value = "是否子题目（0 否 1是）")
    @NotNull(message = "是否子题目（0 否 1是）不能为空")
    @TableField("is_child_field")
    @Excel(name = "是否子题目（0 否 1是）")
    private Integer isChildField;


    @Builder
    public TraceIntoFieldOptionConfig(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long planId, Long formId, String parentFieldId, String parentFieldOptionId, String formFieldId, 
                    String fieldOptionId, Integer isChildField) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.planId = planId;
        this.formId = formId;
        this.parentFieldId = parentFieldId;
        this.parentFieldOptionId = parentFieldOptionId;
        this.formFieldId = formFieldId;
        this.fieldOptionId = fieldOptionId;
        this.isChildField = isChildField;
    }

}
