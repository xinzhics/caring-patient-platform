package com.caring.sass.wx.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "MenuType", description = "菜单类型")
public enum MenuType{


    // 选择身份， 当用户要求重新选择身份时，后端拿到openId，重定向到选择身份页面
    SELECT_ROLE("select/role"),

    // 患者端
    AUTH_LOGIN("login");


    @ApiModelProperty(value = "路径")
    private String path;


    public boolean eq(String val) {
        return val.equals(this.name());
    }


}
