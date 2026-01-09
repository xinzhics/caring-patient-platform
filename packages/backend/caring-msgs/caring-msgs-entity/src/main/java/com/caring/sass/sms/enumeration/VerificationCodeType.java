package com.caring.sass.sms.enumeration;

import com.caring.sass.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * 验证码类型
 *
 * @author caring
 * @date 2019/08/06
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "VerificationCodeType", description = "验证码类型")
public enum VerificationCodeType implements BaseEnum {
    /**
     * 用户注册
     */
    REGISTER_USER("用户注册"),

    DOCTOR_LOGIN("医生登录"),

    RESET_PASSWORD("重置密码"),

    ADMIN_LOGIN("超管人员登录"),
    ;

    @ApiModelProperty(value = "描述")
    private String desc;


    public static VerificationCodeType match(String val, VerificationCodeType def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static VerificationCodeType get(String val) {
        return match(val, null);
    }

    public boolean eq(VerificationCodeType val) {
        return val == null ? false : eq(val.name());
    }

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "REGISTER_USER,DOCTOR_LOGIN,ADMIN_LOGIN", example = "REGISTER_USER,DOCTOR_LOGIN,ADMIN_LOGIN")
    public String getCode() {
        return this.name();
    }
}
