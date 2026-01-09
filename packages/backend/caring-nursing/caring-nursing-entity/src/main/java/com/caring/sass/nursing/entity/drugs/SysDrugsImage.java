package com.caring.sass.nursing.entity.drugs;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * <p>
 * 实体类
 * 药品类别
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "SysDrugsImage", description = "药品图片")
@AllArgsConstructor
@TableName("t_sys_drugs_image")
@Builder
public class SysDrugsImage extends Entity<Long> {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "药品Id")
    @TableField(value = "drugs_id")
    private Long drugsId;

    @ApiModelProperty(value = "图片")
    @Length(max = 500, message = "图片长度不能超过500")
    @TableField(value = "icon")
    private String icon;

    @ApiModelProperty(value = "本机地址")
    @TableField(value = "path")
    private String path;

    @ApiModelProperty(value = "对方的链接")
    @TableField(value = "w_link")
    private String wLink;


}
