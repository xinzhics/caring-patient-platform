package com.caring.sass.nursing.dto.field;

import com.alibaba.fastjson.annotation.JSONField;
import com.caring.sass.nursing.dto.form.DataFeedBack;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FieldObject
 * @Description
 * @Author yangShuai
 * @Date 2022/3/15 16:46
 * @Version 1.0
 */
@Data
@ApiModel(value = "FieldNumber", description = "数字类型字段")
public class FieldNumber {

    @ApiModelProperty(value = "字段属性的唯一标识")
    String id;

    String indefiner;

    String businessId;

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

    @ApiModelProperty(value = "输入时的提示")
    String placeholder;

    @ApiModelProperty(value = "右侧单位")
    String rightUnit;

    @ApiModelProperty(value = "最小值")
    String minValue;

    @ApiModelProperty(value = "最大值")
    String maxValue;

    @ApiModelProperty(value = "数字类型,支持小数")
    Integer canDecimal;

    @ApiModelProperty(value = "保留小数，后几位")
    Integer precision;

    @ApiModelProperty(value = "显示数据反馈")
    Boolean showDataFeedback;

    @ApiModelProperty(value = "数据反馈内容")
    List<DataFeedBack> dataFeedBacks;

    @ApiModelProperty(value = "字段的结果值")
    @JSONField(jsonDirect=true)
    String values = "[" +
            "{" +
            "\"attrValue\":\"\"" +
            "}" +
            "]";

}
