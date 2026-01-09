package com.caring.sass.nursing.dto.field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FieldDESC
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 14:10
 * @Version 1.0
 */
@Data
@ApiModel
public class FieldDesc {

    @ApiModelProperty(value = "字段属性的唯一标识")
    String id;

    @ApiModelProperty(value = "文本描述的标题")
    String label;

    @ApiModelProperty(value = "文本描述说明， 请不要过长")
    String labelDesc;

    @ApiModelProperty(value = "标题对齐方式")
    Integer titleTextAlign;

    @ApiModelProperty(value = "内容对齐方式")
    Integer contentTextAlign;

    @ApiModelProperty(value = "分割线的宽度")
    Integer dividerWidth;

    @ApiModelProperty(value = "标题和内容分割线的样式 默认为 实线")
    Integer dividerStyle;


}
