package com.caring.sass.nursing.entity.form;

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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 表单题目的分组规则
 * </p>
 *
 * @author 杨帅
 * @since 2023-10-11
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_custom_form_fields_group")
@ApiModel(value = "FormFieldsGroup", description = "表单题目的分组规则")
@AllArgsConstructor
public class FormFieldsGroup extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    @Length(max = 255, message = "分组名称长度不能超过255")
    @TableField(value = "group_name", condition = LIKE)
    @Excel(name = "分组名称")
    private String groupName;

    @ApiModelProperty(value = "分组的UUID。给题目关联使用, 创建后不会改变")
    @TableField(value = "field_group_uuid", condition = EQUAL)
    private String fieldGroupUUId;

    /**
     * 分组的排序
     */
    @ApiModelProperty(value = "分组的排序")
    @TableField("group_sort")
    @Excel(name = "分组的排序")
    private Integer groupSort;

    @ApiModelProperty(value = "表单ID")
    @TableField("form_id")
    private Long formId;

    @TableField(exist = false)
    private Float scoreSum = null;

}
