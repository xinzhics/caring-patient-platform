package com.caring.sass.sms;

import com.caring.sass.sms.enumeration.BusinessReminderType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = SmsSignatureConfig.PREFIX)
public class SmsSignatureConfig {

    public static final String PREFIX = "signature.config";

    @ApiModelProperty(value = "saas 普通项目的短信登录签名")
    private String name;


    @ApiModelProperty(value = "saas 过敏项目的短信登录签名")
    private String allergicName;

    @ApiModelProperty(value = "超管登录的验证码模版code")
    private String adminLoginTemplateCode;

    @ApiModelProperty(value = "短信所在的系统环境： p: saas生产, v: saas测试环境， d：大冢， o： 开源环境")
    private String systemEnvironment;


    @ApiModelProperty(value = "患者延迟推送咨询通知模版code")
    private String patient_time_out_consultation_notice;


    @ApiModelProperty(value = "患者今日待办模版code")
    private String patient_today_to_do_list;


    @ApiModelProperty(value = "患者病例讨论开始通知模版code")
    private String patient_notice_of_the_start_case_discussion;

    @ApiModelProperty(value = "患者转诊通知")
    private String patient_referral_notice;


    @ApiModelProperty(value = "患者@咨询通知模版code")
    private String patient_immediately_consultation_notice;


    @ApiModelProperty(value = "医生延时咨询通知")
    private String doctor_time_out_consultation_notice;



    @ApiModelProperty(value = "医生今日待办模版")
    private String doctor_today_to_do_list;



    @Deprecated
    @ApiModelProperty(value = "医生病例讨论开始通知")
    private String doctor_notice_of_the_start_case_discussion;


    @ApiModelProperty(value = "医生病例讨论邀请通知")
    private String doctor_notice_of_invite_case_discussion;

    @ApiModelProperty(value = "信息完整度")
    private String patient_integrity;


    @ApiModelProperty(value = "购药预警")
    private String patient_drug_purchase_warning;



    /**
     * 根据提醒的消息类型。从配置表中获取设置的模版
     * @param businessReminderType
     * @return
     */
    public String getTemplateCodeByReminderType(BusinessReminderType businessReminderType) {
        if (businessReminderType == null) {
            return null;
        }
        switch (businessReminderType) {
            // 患者延时咨询通知
            case PATIENT_TIME_OUT_CONSULTATION_NOTICE:
                return patient_time_out_consultation_notice;
            // 患者今日待办
            case PATIENT_TODAY_TO_DO_LIST:
                return patient_today_to_do_list;
            // 患者病例讨论开始通知
            case PATIENT_NOTICE_OF_THE_START_CASE_DISCUSSION:
                return patient_notice_of_the_start_case_discussion;
            // 患者转诊通知
            case PATIENT_REFERRAL_NOTICE:
                return patient_referral_notice;

            // 患者@咨询通知
            case PATIENT_IMMEDIATELY_CONSULTATION_NOTICE:
                return patient_immediately_consultation_notice;

            // 医生延时咨询通知
            case DOCTOR_TIME_OUT_CONSULTATION_NOTICE:
                return doctor_time_out_consultation_notice;

            // 医生今日待办-预约审核
            case DOCTOR_TODAY_TO_DO_LIST:
                return doctor_today_to_do_list;

            // 医生病例讨论开始通知
            case DOCTOR_NOTICE_OF_THE_START_CASE_DISCUSSION:
                return doctor_notice_of_the_start_case_discussion;

            // 医生病例讨论邀请通知
            case DOCTOR_NOTICE_OF_INVITE_CASE_DISCUSSION:
                return doctor_notice_of_invite_case_discussion;

            case PATIENT_INTEGRITY:
                return patient_integrity;

            case PATIENT_DRUG_PURCHASE_WARNING:
                return patient_drug_purchase_warning;
        }
        return null;
    }

}
