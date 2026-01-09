package com.caring.sass.tenant.enumeration;

import com.caring.sass.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "SequenceEnum", description = "序号类型-枚举")
public enum SequenceEnum implements BaseEnum {
    /**
     * TENANT_CODE="项目编码"
     */
    TENANT_CODE("项目编码"),

    ORG_CODE("组织编码"),

    OTHER_CODE("其他编码"),

    APP_VERSION("app版本号");

    @ApiModelProperty(value = "描述")
    private String desc;

    public static SequenceEnum match(String val, SequenceEnum def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }
    
    public static SequenceEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(SequenceEnum val) {
        return val != null && eq(val.name());
    }
    
    @Override
    public String getCode() {
        return this.name();
    }
}
