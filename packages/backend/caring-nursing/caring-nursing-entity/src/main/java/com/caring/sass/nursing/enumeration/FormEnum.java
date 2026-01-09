package com.caring.sass.nursing.enumeration;

import com.caring.sass.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * @author xinzh
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "FormEnum", description = "表单类型-枚举")
public enum FormEnum implements BaseEnum {

    /**
     * HEALTH_RECORD="疾病信息"
     */
    HEALTH_RECORD("疾病信息"),
    /**
     * BASE_INFO="基本信息"
     */
    BASE_INFO("基本信息"),

    /**
     * NURSING_PLAN="护理计划"
     */
    NURSING_PLAN("护理计划");

    @ApiModelProperty(value = "描述")
    private String desc;


    public static FormEnum match(String val, FormEnum def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static FormEnum get(String val) {
        return match(val, null);
    }


    public boolean eq(FormEnum val) {
        return val != null && eq(val.name());
    }

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "HEALTH_RECORD,BASE_INFO,NURSING_PLAN", example = "HEALTH_RECORD")
    public String getCode() {
        return this.name();
    }

}
