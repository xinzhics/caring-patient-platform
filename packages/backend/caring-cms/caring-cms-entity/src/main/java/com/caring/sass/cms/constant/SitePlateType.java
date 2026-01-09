package com.caring.sass.cms.constant;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @className: SitePlateType
 * @author: 杨帅
 * @date: 2023/9/7
 */
@Getter
@ApiModel(value = "SitePlateType", description = "建站版本内容类型")
public enum SitePlateType {

    IMAGE,

    ARTICLE,

    VIDEO;

}
