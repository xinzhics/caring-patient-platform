package com.caring.sass.common.enums;

import com.caring.sass.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * HTTP方法枚举
 *
 * @author caring
 */
@Getter
@ApiModel(value = "拥有者类型", description = "拥有者类型-枚举")
@AllArgsConstructor
@NoArgsConstructor
public enum OwnerTypeEnum implements BaseEnum {
    /**
     * SYS:SYS
     */
    SYS("SYS"),
    /**
     * TENANT:TENANT
     */
    TENANT("TENANT"),
    ;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static OwnerTypeEnum match(String val, OwnerTypeEnum def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static OwnerTypeEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(OwnerTypeEnum val) {
        return val == null ? false : eq(val.name());
    }

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "SYS,TENANT", example = "SYS")
    public String getCode() {
        return this.name();
    }
}
