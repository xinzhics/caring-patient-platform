package com.caring.sass.nursing.entity.tag;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 标签管理
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_tag")
@ApiModel(value = "Tag", description = "标签管理")
@AllArgsConstructor
public class Tag extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    @Length(max = 255, message = "标签名称长度不能超过255")
    @TableField(value = "name_", condition = LIKE)
    @Excel(name = "标签名称")
    private String name;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    @TableField("order_")
    @Excel(name = "优先级")
    private Integer order;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Length(max = 255, message = "图标长度不能超过255")
    @TableField(value = "icon", condition = LIKE)
    @Excel(name = "图标")
    private String icon;

    @ApiModelProperty(value = "处理标签绑定 0：未处理 2：处理中 1：完成 ")
    @TableField(value = "handle_attr_bind", condition = EQUAL)
    private Integer handleAttrBind;

    @TableField(exist = false)
    private List<Attr> tagAttr;

    @TableField(exist = false)
    private Integer countPatient;

    public void setTagAttrAdd(List<Attr> tagAttr) {
        if (!CollectionUtils.isEmpty(tagAttr)) {
            if (CollectionUtils.isEmpty(this.tagAttr)) {
                this.tagAttr = new ArrayList<>(tagAttr.size());
            }
            this.tagAttr.addAll(tagAttr);
        }
    }


}
