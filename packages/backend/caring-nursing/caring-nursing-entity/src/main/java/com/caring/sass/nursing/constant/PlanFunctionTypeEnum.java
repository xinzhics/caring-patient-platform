package com.caring.sass.nursing.constant;

import com.caring.sass.base.BaseEnum;
import com.caring.sass.nursing.enumeration.FollowUpPlanTypeEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * 功能配置的各子项类型
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PlanFunctionTypeEnum", description = "功能配置类型")
public enum PlanFunctionTypeEnum implements BaseEnum {

    MEDICATION("用药提醒"),


    REVIEW_MANAGE("复查提醒"),


    HEALTH_LOG( "健康日志"),


    REFERRAL_SERVICE("转诊管理"),


    LEARNING_PLAN( "学习计划"),


    BOOKING_MANAGEMENT("预约管理"),


    CASE_DISCUSSION("病例讨论"),


    ONLINE_CONSULTATION("在线咨询"),


    INDICATOR_MONITORING("指标监测"),

    BLOOD_PRESSURE("血压提醒"),

    BLOOD_SUGAR("血糖提醒"),

    INTERACTIVE_MESSAGE("互动消息"),

    CUSTOM_FOLLOW_UP("自定义随访");


    @ApiModelProperty(value = "描述")
    private String desc;

    public static PlanFunctionTypeEnum getEnumByPlanType(Integer planType, String followUpPlanType) {
        if (planType == null) {
            if (FollowUpPlanTypeEnum.CARE_PLAN.operateType.equals(followUpPlanType)) {
                return CUSTOM_FOLLOW_UP;
            } else if (FollowUpPlanTypeEnum.MONITORING_DATA.operateType.equals(followUpPlanType)) {
                return INDICATOR_MONITORING;
            }
        } else {
            if (PlanEnum.HEALTH_LOG.getCode().equals(planType)) {
                return HEALTH_LOG;
            }
            if (PlanEnum.REVIEW_REMIND.getCode().equals(planType)) {
                return REVIEW_MANAGE;
            }
            if (PlanEnum.LEARN_PLAN.getCode().equals(planType)) {
                return LEARNING_PLAN;
            }
            if (PlanEnum.BLOOD_PRESSURE.getCode().equals(planType)) {
                return BLOOD_PRESSURE;
            }
            if (PlanEnum.BLOOD_SUGAR.getCode().equals(planType)) {
                return BLOOD_SUGAR;
            }
        }
        return null;
    }

    public static String getNameByPlanType(Integer planType, String followUpPlanType) {
        if (planType == null) {
            if (FollowUpPlanTypeEnum.CARE_PLAN.operateType.equals(followUpPlanType)) {
                return FollowUpPlanTypeEnum.CARE_PLAN.operateTypeName;
            } else if (FollowUpPlanTypeEnum.MONITORING_DATA.operateType.equals(followUpPlanType)) {
                return FollowUpPlanTypeEnum.MONITORING_DATA.operateTypeName;
            }
        } else {
            if (PlanEnum.HEALTH_LOG.getCode().equals(planType)) {
                return HEALTH_LOG.desc;
            }
            if (PlanEnum.REVIEW_REMIND.getCode().equals(planType)) {
                return REVIEW_MANAGE.desc;
            }
            if (PlanEnum.LEARN_PLAN.getCode().equals(planType)) {
                return LEARNING_PLAN.desc;
            }
        }
        return null;
    }

    public static PlanFunctionTypeEnum match(String val, PlanFunctionTypeEnum def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static PlanFunctionTypeEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(PlanFunctionTypeEnum val) {
        return val == null ? false : eq(val.name());
    }

    @Override
    @ApiModelProperty(value = "编码", allowableValues = "MEDICATION,REVIEW_MANAGE,HEALTH_LOG,REFERRAL_SERVICE,LEARNING_PLAN,BOOKING_MANAGEMENT,CASE_DISCUSSION,ONLINE_CONSULTATION,INDICATOR_MONITORING,CUSTOM_FOLLOW_UP")
    public String getCode() {
        return this.name();
    }
}
