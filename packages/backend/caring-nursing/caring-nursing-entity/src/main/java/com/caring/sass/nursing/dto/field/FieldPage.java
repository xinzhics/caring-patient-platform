package com.caring.sass.nursing.dto.field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FieldPage
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 14:10
 * @Version 1.0
 */
@Data
@ApiModel
public class FieldPage {

    @ApiModelProperty(value = "字段属性的唯一标识")
    String id;

    @ApiModelProperty(value = "分页按钮的 样式")
    String pageType;

    @ApiModelProperty(value = "下一页")
    String label;

    @ApiModelProperty(value = "下一页")
    String labelDesc;

    @ApiModelProperty(value = "是否可以返回当前页")
    Integer canBackPrevious;

}
