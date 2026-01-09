package com.caring.sass.nursing.dto.field;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FieldMultiLineText
 * @Description
 * @Author yangShuai
 * @Date 2022/3/15 16:55
 * @Version 1.0
 */
@Data
@ApiModel(value = "FieldMultiLineText", description = "多行文本字段")
public class FieldMultiLineText{


    @ApiModelProperty(value = "字段属性的唯一标识")
    String id;

    @ApiModelProperty(value = "标题")
    String label;

    @ApiModelProperty(value = "标题的描述")
    String labelDesc;

    @ApiModelProperty(value = "是否必填")
    Boolean required;

    @ApiModelProperty(value = "字段标识", dataType = "FormFieldExactType")
    String exactType;

    @ApiModelProperty(value = "字段类型", dataType = "FormWidget")
    FormWidget widgetType;

    @ApiModelProperty(value = "医助可以修改（1： 是 ）", dataType = "ispdatable")
    Integer isUpdatable;

    @ApiModelProperty(value = "字段的结果值")
    @JSONField(jsonDirect=true)
    String values = "[" +
            "{" +
            "\"attrValue\":\"\"" +
            "}" +
            "]";


    @ApiModelProperty(value = "输入时的提示")
    String placeholder;

    @ApiModelProperty(value = "最大可输入长度。 限 单行 多行文本使用")
    Integer maxEnterNumber;



}
