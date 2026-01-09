package com.caring.sass.cms.constant;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @className: SiteTitlePositionEnum
 * @author: 杨帅
 * @date: 2023/9/5
 */
@Getter
@ApiModel(value = "SiteTitlePositionEnum", description = "主副标题显示位置")
public enum SiteTitlePositionEnum {

    LEFT,

    CENTER,
        ;


}