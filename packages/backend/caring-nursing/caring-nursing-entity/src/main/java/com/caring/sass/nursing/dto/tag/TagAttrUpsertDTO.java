package com.caring.sass.nursing.dto.tag;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * @author leizhi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TagAttrDTO", description = "标签属性更新插入")
public class TagAttrUpsertDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "标签属性的来源(基本信息， 疾病信息， 监测计划， 药品)")
    @Excel(name = "标签属性的来源")
    private String attrSource;

    @ApiModelProperty(value = "监测计划ID")
    @Excel(name = "监测计划ID")
    private Long planId;

    /**
     * 属性ID
     */
    @ApiModelProperty(value = "表单属性ID或者药品的id")
    @Length(max = 32, message = "属性ID长度不能超过32")
    private String attrId;

    /**
     * 属性名称
     */
    @ApiModelProperty(value = "表单属性名称")
    @Length(max = 255, message = "属性名称长度不能超过255")
    private String attrName;
    /**
     * 属性值
     */
    @ApiModelProperty(value = "目标属性值（医院ID、选项ID、 用药状态）")
    @Length(max = 255, message = "目标属性值长度不能超过255")
    private String attrValue;
    /**
     * 属性最小值
     */
    @ApiModelProperty(value = "目标属性最小值")
    @Length(max = 255, message = "目标属性最小值长度不能超过255")
    private String attrValueMin;
    /**
     * 属性最大值
     */
    @ApiModelProperty(value = "目标属性最大值")
    @Length(max = 255, message = "目标属性最大值长度不能超过255")
    private String attrValueMax;

    /**
     * 标签Id
     */
    @ApiModelProperty(value = "标签Id")
    private Long tagId;

    @ApiModelProperty(value = "表单属性类型")
    private String widgetType;

}
