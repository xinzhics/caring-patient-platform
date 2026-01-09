package com.caring.sass.nursing.entity.drugs;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName SysDrugsCategoryLink
 * @Description
 * @Author yangShuai
 * @Date 2021/3/1 16:13
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_sys_drugs_category_link")
@ApiModel(value = "SysDrugsCategoryLink", description = "药品类别关联表")
@AllArgsConstructor
public class SysDrugsCategoryLink extends Entity<Long> {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "药品Id")
    @TableField(value = "drugs_id", condition = EQUAL)
    private Long drugsId;

    @ApiModelProperty(value = "父级类别Id")
    @TableField(value = "category_parent_id", condition = EQUAL)
    private Long categoryParentId;

    @TableField(exist = false)
    private String parentCategoryName;

    @ApiModelProperty(value = "类别Id")
    @TableField(value = "category_id", condition = EQUAL)
    private Long categoryId;

    @TableField(exist = false)
    private String categoryName;


    @TableField(exist = false)
    private String name;
}
