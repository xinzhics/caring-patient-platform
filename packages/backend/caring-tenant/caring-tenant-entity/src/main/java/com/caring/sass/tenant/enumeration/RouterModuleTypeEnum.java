package com.caring.sass.tenant.enumeration;

import com.caring.sass.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * @className: RouterModuleTypeEnum
 * @author: 杨帅
 * @date: 2023/6/27
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "RouterModuleTypeEnum", description = "患者个人中心-枚举")
public enum  RouterModuleTypeEnum implements BaseEnum {

    BASE_INFO("基本信息"),

    MY_FEATURES("我的功能"),

    MY_FILE("我的档案");

    public static RouterModuleTypeEnum matchEnum(String dictItemType) {
        switch (dictItemType) {
            // 基本信息
            case "BASE_INFO":  {
                return RouterModuleTypeEnum.BASE_INFO;
            }
            // 我的药箱
            case "MEDICINE":
            // 在线预约
            case "RESERVATION_INDEX":
            // 随访
            case "FOLLOW_UP":
            // 智能提醒
            case "REMIND": {
                return RouterModuleTypeEnum.MY_FEATURES;
            }
            // 在线咨询
            case "IM_INDEX": {
                return null;
            }
            default: return RouterModuleTypeEnum.MY_FILE;
        }

    }

    @ApiModelProperty(value = "描述")
    private String desc;

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "BASE_INFO,MY_FEATURES,MY_FILE")
    public String getCode() {
        return this.name();
    }

    public static RouterModuleTypeEnum match(String val, RouterModuleTypeEnum def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static RouterModuleTypeEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(RouterModuleTypeEnum val) {
        return val == null ? false : eq(val.name());
    }


}
