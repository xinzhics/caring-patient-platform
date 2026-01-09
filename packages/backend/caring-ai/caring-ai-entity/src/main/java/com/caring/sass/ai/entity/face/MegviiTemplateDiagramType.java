package com.caring.sass.ai.entity.face;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 模版图分类
 * </p>
 *
 * @author 杨帅
 * @since 2024-04-30
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_megvii_template_diagram_type", autoResultMap = true)
@ApiModel(value = "MegviiTemplateDiagramType", description = "模版图分类")
@AllArgsConstructor
public class MegviiTemplateDiagramType extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    @TableField("order_")
    @Excel(name = "优先级")
    private Integer order;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    @Length(max = 50, message = "分类名称长度不能超过50")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "分类名称")
    private String name;

    @ApiModelProperty(value = "Male 男, Female 女")
    @Length(max = 255, message = "Male 男, Female 女长度不能超过255")
    @TableField(value = "gender", condition = LIKE)
    @Excel(name = "Male 男, Female 女")
    private String gender;

    @ApiModelProperty(value = "首张图路径")
    @TableField(exist = false)
    private String imageObsUrl;

    @ApiModelProperty(value = "免费 0 要收费， 1 免费 ")
    @TableField("free_")
    private Integer free;


    @ApiModelProperty(value = "前端显示的价格，单位分")
    @TableField("cost_")
    private Integer cost;


}
