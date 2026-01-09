package com.caring.sass.tenant.dto.config;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xinzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "WxMenuJsonDTO", description = "微信菜单json传输对象")
public class WxMenuJsonDTO implements Serializable {


    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "微信菜单json")
    private String wxMenuJson;
}
