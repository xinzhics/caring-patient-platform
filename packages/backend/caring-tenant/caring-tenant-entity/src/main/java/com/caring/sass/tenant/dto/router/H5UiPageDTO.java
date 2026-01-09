package com.caring.sass.tenant.dto.router;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "H5UiPageDTO", description = "UI组件")
public class H5UiPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 64, message = "名称长度不能超过64")
    private String name;
    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    @Length(max = 255, message = "属性值长度不能超过255")
    private String attribute1;
    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    @Length(max = 255, message = "属性值长度不能超过255")
    private String attribute2;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortValue;
    /**
     * ui组件类型：1图片 2其它
     */
    @ApiModelProperty(value = "ui组件类型：1图片 2其它")
    private Integer type;

}
