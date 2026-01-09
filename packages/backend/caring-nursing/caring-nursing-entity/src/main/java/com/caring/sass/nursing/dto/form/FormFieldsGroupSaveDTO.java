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
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FormFieldsGroupSaveDTO", description = "表单题目的分组规则")
public class FormFieldsGroupSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    @Length(max = 255, message = "分组名称长度不能超过255")
    private String groupName;

    @ApiModelProperty(value = "分组的UUID。给题目关联使用, 创建后不会改变")
    private String fieldGroupUUId;
    /**
     * 分组所在的表单ID
     */
    @ApiModelProperty(value = "分组所在的表单ID")
    private Long formId;
    /**
     * 分组的排序
     */
    @ApiModelProperty(value = "分组的排序")
    private Integer groupSort;


    @ApiModelProperty(value = "租户")
    private String tenantCode;
}
