package com.caring.sass.nursing.dto.field;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FieldObject
 * @Description
 * @Author yangShuai
 * @Date 2022/3/15 16:46
 * @Version 1.0
 */
@Data
@ApiModel(value = "FieldImageUpload", description = "图片上传字段")
public class FieldImageUpload {

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

    @ApiModelProperty(value = "头像组件不支持 上传多个图片 （是1， 否2）")
    Integer uploadMany;

    @ApiModelProperty(value = "头像组件不支持 上传数量 1， 2 ，3，或者更大")
    Integer uploadNumber;

    @ApiModelProperty(value = "字段的结果值")
    @JSONField(jsonDirect=true)
    String values = "[\n" +
            "{\n" +
            "\"attrValue\":\"https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/0112%2F2022%2F03%2Ff36315b8-31f5-4d9f-8bfd-a99105e685b2.jpg\"\n" +
            "},\n" +
            "{\n" +
            "\"attrValue\":\"https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/0112%2F2022%2F03%2Ff36315b8-31f5-4d9f-8bfd-a99105e685b2.jpg\"\n" +
            "}\n" +
            "]";




}
