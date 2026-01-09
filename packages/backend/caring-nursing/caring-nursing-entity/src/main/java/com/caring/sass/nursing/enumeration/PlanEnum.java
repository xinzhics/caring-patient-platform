package com.caring.sass.nursing.enumeration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author xinzh
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PlanEnum", description = "护理计划-枚举")
public enum PlanEnum {
    /**
     * 血压提醒
     */
    BLOOD_PRESSURE(1, "血压提醒"),

    /**
     * 血糖提醒
     */
    BLOOD_SUGAR(2, "血糖提醒"),

    /**
     * 复查提醒
     */
    REVIEW_REMIND(3, "复查提醒"),

    /**
     * 用药提醒
     */
    MEDICATION_REMIND(4, "用药提醒"),

    /**
     * 健康日志
     */
    HEALTH_LOG(5, "健康日志"),

    /**
     * 学习计划
     */
    LEARN_PLAN(6, "学习计划"),

    /**
     * 营养食谱
     */
    @Deprecated
    NUTRIYIOUS_DIET(7, "营养食谱"),

    /**
     * 疼痛日志
     */
    @Deprecated
    PAIN_LOG(8, "疼痛日志"),

    /**
     * 检验数据
     */
    @Deprecated
    CHECK_DATA(9, "检验数据"),


    /**
     * 自定义护理计划
     * TODO: 自定义护理计划 数据库存的是null。前端传参也是null，这里先这样
     */
    CUSTOM_PLAN(null, "自定义护理计划"),
    ;

    private Integer code;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static PlanEnum getPlanEnum(Integer code) {
        if (code == null) {
            return CUSTOM_PLAN;
        }
        for (PlanEnum value : values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        throw new RuntimeException("没有这个类型");
    }

}
