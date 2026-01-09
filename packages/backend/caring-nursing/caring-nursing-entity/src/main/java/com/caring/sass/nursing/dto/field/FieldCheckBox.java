package com.caring.sass.nursing.dto.field;

import com.alibaba.fastjson.annotation.JSONField;
import com.caring.sass.nursing.dto.form.FormOptions;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FieldCheckBox
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 9:41
 * @Version 1.0
 */
@Data
@ApiModel(value = "FieldCheckBox", description = "多选题字段")
public class FieldCheckBox{


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


    @ApiModelProperty(value = "单选 多选才会使用，是否增加其它选项 ( 1 是, 2)")
    Integer hasOtherOption = 2;

    @ApiModelProperty(value = "其他选项 保存的值")
    String otherValue;

    @ApiModelProperty(value = "其他输入框可以输入的长度")
    Integer otherValueSize = 120;

    @ApiModelProperty(value = "其他选项输入框是否必填 ( 1 是,  2 )")
    Integer otherEnterRequired = 2;

    @ApiModelProperty(value = "在所有的 选项后面 有输入框")
    Integer itemAfterHasEnter = 2;

    @ApiModelProperty(value = "选项输入框 后面得输入框是否为 必填")
    Integer itemAfterHasEnterRequired = 2;

    @ApiModelProperty(value = "选项输入框 后面得输入框 可以写多少字")
    Integer itemAfterHasEnterSize = 120;

    @ApiModelProperty(value = "单选题的选项")
    List<FormOptions> options;

    @ApiModelProperty(value = "字段的结果值")
    @JSONField(jsonDirect=true)
    String values = "[" +
            "{" +
            "\"valueText\":\"女\"," +
            "\"desc\":\"选项备注\"," +
            "\"attrValue\":\"2e135e21519b4f5a8e790e9a5d8ab9a2\"" +
            "\"questions\":\"[]\" " +
            "}," +
            "{" +
            "\"valueText\":\"男\"," +
            "\"desc\":\"选项备注\"," +
            "\"attrValue\":\"2e135e21519b4f5a8e790e9a5d8ab2a2\"" +
            "\"questions\":\"[]\" " +
            "}" +
            "]";




}
