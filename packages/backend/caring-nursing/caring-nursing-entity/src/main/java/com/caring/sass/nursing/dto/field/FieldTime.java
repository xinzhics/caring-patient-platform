package com.caring.sass.nursing.dto.field;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FieldTime
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 13:56
 * @Version 1.0
 */
@Data
@ApiModel
public class FieldTime {

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

    @ApiModelProperty(value = "医助可以修改（1： 是 ）", dataType = "isUpdatable")
    Integer isUpdatable;

    @ApiModelProperty(value = "字段的结果值")
    @JSONField(jsonDirect=true)
    String values = "[" +
            "{" +
            "\"attrValue\":\"\"" +
            "}" +
            "]";


}
