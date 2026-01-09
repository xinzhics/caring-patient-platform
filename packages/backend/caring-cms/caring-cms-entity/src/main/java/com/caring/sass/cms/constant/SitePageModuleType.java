package com.caring.sass.cms.constant;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @className: SitePageTemplateType
 * @author: 杨帅
 * @date: 2023/9/5
 */
@Getter
@ApiModel(value = "SitePageTemplateType", description = "建站页面模板类型-枚举")
public enum SitePageModuleType {

    /**
     * 文本组件
     */
    SITE_TEXT,

    /**
     * 图片组件
     */
    PICTURE,
    /**
     * 视频组件
     */
    VIDEO,
    /**
     * 按钮组件
     */
    BUTTON,
    /**
     * 多图组件
     */
    MULTI_GRAPH,
    /**
     * 轮播图组件
     */
    SWIPER,
    /**
     * 文章组件
     */
    ARTICLE,
    /**
     * 导航组件
     */
    NAVIGATION,
    /**
     * 图形组件
     */
    GRAPHICAL,
    /**
     * 多视频组件
     */
    MULTIPLE_VIDEOS,
    /**
     * 多功能导航组件
     */
    MULTIFUNCTIONAL_NAVIGATION,
    /**
     * 搜索组件
     */
    SEARCH_FOR,
    ;


}
