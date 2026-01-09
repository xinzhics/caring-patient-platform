package com.caring.sass.cms.constant;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @className: SiteJumpTypeEnum
 * @author: 杨帅
 * @date: 2023/9/7
 */
@Getter
@ApiModel(value = "SiteJumpTypeEnum", description = "建站版本内容类型")
public enum SiteJumpTypeEnum {

    /**
     * 页面
     */
    PAGE,

    /**
     * 小程序
     */
    APPLET,

    /**
     * 外链
     */
    WEBSITE,

    /**
     * 文章
     */
    ARTICLE,

    /**
     * 视频
     */
    VIDEO,
    ;

}

