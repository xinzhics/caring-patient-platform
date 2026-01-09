package com.caring.sass.nursing.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 标签属性
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "AttrSaveDTO", description = "标签属性")
public class AttrSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 属性ID
     */
    @ApiModelProperty(value = "属性ID")
    @Length(max = 32, message = "属性ID长度不能超过32")
    private String attrId;
    /**
     * 属性名称
     */
    @ApiModelProperty(value = "属性名称")
    @Length(max = 255, message = "属性名称长度不能超过255")
    private String attrName;
    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    @Length(max = 255, message = "属性值长度不能超过255")
    private String attrValue;
    /**
     * 属性最小值
     */
    @ApiModelProperty(value = "属性最小值")
    @Length(max = 255, message = "属性最小值长度不能超过255")
    private String attrValueMin;
    /**
     * 属性最大值
     */
    @ApiModelProperty(value = "属性最大值")
    @Length(max = 255, message = "属性最大值长度不能超过255")
    private String attrValueMax;
    /**
     * 标签Id
     */
    @ApiModelProperty(value = "标签Id")
    private Long tagId;
    @ApiModelProperty(value = "")
    @Length(max = 50, message = "长度不能超过50")
    private String widgetType;

}
