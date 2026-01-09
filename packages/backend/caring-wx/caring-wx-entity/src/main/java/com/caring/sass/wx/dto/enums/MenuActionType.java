package com.caring.sass.wx.dto.enums;

import com.caring.sass.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * 微信菜单相应类型
 *
 * @author xinzh
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "MenuActionType", description = "微信菜单动作类型")
public enum MenuActionType implements BaseEnum {


    /**
     * view="表示网页类型"
     */
    view("表示网页类型"),
    
    /**
     * click="点击类型"
     */
    click("点击类型"),

    /**
     * miniprogram="护理计划"
     */
    miniprogram("小程序类型");

    @ApiModelProperty(value = "描述")
    private String desc;


    public static MenuActionType match(String val, MenuActionType def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static MenuActionType get(String val) {
        return match(val, null);
    }


    public boolean eq(MenuActionType val) {
        return val == null ? false : eq(val.name());
    }

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "view,click,miniprogram", example = "view")
    public String getCode() {
        return this.name();
    }
}
