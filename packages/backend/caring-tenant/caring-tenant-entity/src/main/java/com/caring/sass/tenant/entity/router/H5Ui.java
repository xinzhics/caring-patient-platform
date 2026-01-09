package com.caring.sass.tenant.entity.router;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * UI组件
 * </p>
 *
 * @author leizhi
 * @since 2021-03-25
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_h5_ui")
@ApiModel(value = "H5Ui", description = "UI组件")
@AllArgsConstructor
public class H5Ui extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 64, message = "名称长度不能超过64")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "名称")
    private String name;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    @NotEmpty(message = "编码不能为空")
    @Length(max = 64, message = "编码长度不能超过64")
    @TableField(value = "code", condition = LIKE)
    @Excel(name = "编码")
    private String code;


    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    @Length(max = 255, message = "属性值长度不能超过255")
    @TableField(value = "attribute_1", condition = LIKE)
    @Excel(name = "属性值1")
    private String attribute1;

    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    @Length(max = 255, message = "属性值长度不能超过255")
    @TableField(value = "attribute_2", condition = LIKE)
    @Excel(name = "属性值2")
    private String attribute2;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @TableField("sort_value")
    @Excel(name = "排序")
    private Integer sortValue;

    /**
     * ui组件类型：1图片 2其它
     */
    @ApiModelProperty(value = "ui组件类型：1图片 2其它")
    @TableField("type_")
    private Integer type;


    @Builder
    public H5Ui(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime,
                String name, String code, String attribute1, String attribute2, Integer sortValue, Integer type) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.name = name;
        this.code = code;
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
        this.sortValue = sortValue;
        this.type = type;
    }

}
