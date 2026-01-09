package com.caring.sass.nursing.dto.field;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FieldDate
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 13:56
 * @Version 1.0
 */
@Data
@ApiModel(value = "FieldDate", description = "时间类型字段")
public class FieldDate {


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

    @ApiModelProperty(value = "最小时间")
    String minValue = "1945-01-01";

    @ApiModelProperty(value = "默认选中时间")
    String defaultValue;

    @ApiModelProperty(value = "时间类型，限定选择时间")
    Integer defineChooseDate = 2;

    @ApiModelProperty(value = "只能选当前及以前的时间 1  只能选当前 及 以后的时间 2")
    Integer defineChooseDateType;

    @ApiModelProperty(value = "字段的结果值")
    @JSONField(jsonDirect=true)
    String values = "[" +
            "{" +
            "\"attrValue\":\"\"" +
            "}" +
            "]";


}
