package com.caring.sass.cms.constant;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @className: SiteImageFillStyle
 * @author: 杨帅
 * @date: 2023/9/5
 */
@Getter
@ApiModel(value = "SiteImageFillStyle", description = "组件图片缩放方式")
public enum SiteImageFillStyle{


    /**
     * 填充
     */
    FILL,

    /**
     * 拉伸
     */
    DRAW,

    /**
     * 不平铺
     */
    NO_REPEAT,
    ;

}
