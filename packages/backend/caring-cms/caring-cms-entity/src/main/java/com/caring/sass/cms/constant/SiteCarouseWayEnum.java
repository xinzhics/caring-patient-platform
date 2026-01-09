package com.caring.sass.cms.constant;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @className: SiteCarouseWayEnum
 * @author: 杨帅
 * @date: 2023/9/5
 */
@Getter
@ApiModel(value = "SiteCarouseWayEnum", description = "轮播图组件轮播方式")
public enum SiteCarouseWayEnum {


    /**
     * 默认轮播
     */
    DEFAULT_ROTATION,

    /**
     * 自动轮播
     */
    AUTOMATIC,

    /**
     * 手动轮播
     */
    HAND_MOVEMENT,
    ;

}
