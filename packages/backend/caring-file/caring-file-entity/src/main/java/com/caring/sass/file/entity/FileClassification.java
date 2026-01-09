package com.caring.sass.file.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.file.constant.ClassificationBelongEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 图片分组
 * </p>
 *
 * @author 杨帅
 * @since 2022-08-29
 */
@Data
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("f_file_classification")
@ApiModel(value = "FileClassification", description = "图片分组")
@AllArgsConstructor
public class FileClassification extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    @TableField("classification_name")
    @Excel(name = "分组名称")
    private String classificationName;

    /**
     * 分组的排序
     */
    @ApiModelProperty(value = "分组的排序")
    @TableField("classification_sort")
    @Excel(name = "分组的排序")
    private Integer classificationSort;

    /**
     * 分组的归属(公众图片，我的图片)
     */
    @ApiModelProperty(value = "分组的归属(公众图片，我的图片)")
    @Length(max = 20, message = "分组的归属(公众图片，我的图片)长度不能超过20")
    @TableField(value = "classification_belong", condition = LIKE)
    @Excel(name = "分组的归属(公众图片，我的图片)")
    private String classificationBelong;



}
