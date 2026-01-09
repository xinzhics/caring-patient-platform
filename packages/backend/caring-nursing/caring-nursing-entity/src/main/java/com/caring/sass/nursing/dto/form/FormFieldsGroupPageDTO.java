package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@ApiModel(value = "FormFieldsGroupPageDTO", description = "表单题目的分组规则")
public class FormFieldsGroupPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组所在的表单ID
     */
    @ApiModelProperty(value = "分组所在的表单ID")
    private Long formGroupId;
    /**
     * 分组的排序
     */
    @ApiModelProperty(value = "分组的排序")
    private Integer groupSort;


    @ApiModelProperty(value = "租户")
    private String tenantCode;

}
