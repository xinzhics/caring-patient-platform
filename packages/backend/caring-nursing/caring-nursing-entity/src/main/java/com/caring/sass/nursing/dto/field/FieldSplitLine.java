package com.caring.sass.nursing.dto.field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FieldSplitLine
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 14:09
 * @Version 1.0
 */
@Data
@ApiModel
public class FieldSplitLine {


    @ApiModelProperty(value = "字段属性的唯一标识")
    String id;

    @ApiModelProperty(value = "分割线宽度")
    Integer dividerWidth;

    @ApiModelProperty(value = "分割线的样式 默认为 细线")
    String dividerStyle;

}
