package com.caring.sass.cms.dto.site;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: SIteJsonStyleDTO
 * @author: 杨帅
 * @date: 2023/9/5
 */
@Data
public class SiteJsonStyleDTO {

    @ApiModelProperty("定位方式")
    String position;
    @ApiModelProperty("定位距离头部")
    String top;
    @ApiModelProperty("定位距离左边")
    String left;
    @ApiModelProperty("定位距离右边")
    String right;
    @ApiModelProperty("定位距离底部")
    String bottom;
    @ApiModelProperty("定位层级")
    String zIndex;

    @ApiModelProperty("宽度")
    String width;
    @ApiModelProperty("高度")
    String height;

    @ApiModelProperty("文本字体")
    String fontFamily;
    @ApiModelProperty("文字颜色")
    String color;
    @ApiModelProperty("文字加粗")
    String fontWeight;
    @ApiModelProperty("文字样式(斜体)")
    String fontStyle;
    @ApiModelProperty("文字样式(下划线)")
    String textDecoration;
    @ApiModelProperty("文字居左，居右,两端对齐")
    String textAlign;
    @ApiModelProperty("文字最后一行的居中居中居右")
    String textAlignLast;

    @ApiModelProperty("文字居左，居右")
    String marginLeft;

    @ApiModelProperty("文字居左，居右")
    String marginRight;

    @ApiModelProperty("文字 字间距")
    String letterSpacing;


    @ApiModelProperty("文字 行距")
    String lineHeight;
    @ApiModelProperty("文字 行距")
    String wordSpacing;

    @ApiModelProperty("边框")
    String border;
    @ApiModelProperty("边框半径")
    String borderRadius;


    @ApiModelProperty("背景")
    String background;

    @ApiModelProperty("背景颜色")
    String backgroundColor;

}
