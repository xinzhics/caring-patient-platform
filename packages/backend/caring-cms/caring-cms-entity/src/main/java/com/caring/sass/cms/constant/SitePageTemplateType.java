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
public enum SitePageTemplateType {


    /**
     * 单页模板
     */
    PAGE,
    /**
     * 文件夹模板
     */
    FOLDER,
    ;


}
