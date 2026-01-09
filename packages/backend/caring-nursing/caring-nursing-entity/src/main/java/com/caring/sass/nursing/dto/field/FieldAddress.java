package com.caring.sass.nursing.dto.field;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FieldAddress
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 14:30
 * @Version 1.0
 */
@Data
@ApiModel(value = "FieldAddress", description = "地址组件")
public class FieldAddress {

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

    @ApiModelProperty(value = "地址组件是否有详情输入框")
    Integer hasAddressDetail;

    @ApiModelProperty(value = "地址组件 详情输入框是否必填")
    Integer hasAddressDetailRequired;

    @ApiModelProperty(value = "详情地址输入框最多多少字")
    Integer addressDetailSize;

    @ApiModelProperty(value = "输入时的提示")
    String placeholder;

    @ApiModelProperty(value = "字段的结果值")
    @JSONField(jsonDirect=true)
    String values = "[\n" +
            "{\n" +
            "\"attrValue\":[\n" +
            "\"北京市\",\n" +
            "\"北京市\",\n" +
            "\"东城区\"\n" +
            "]\n" +
            "}\n" +
            "]";

    @ApiModelProperty(value = "详情输入框的值")
    String value;


}
