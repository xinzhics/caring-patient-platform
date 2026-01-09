package com.caring.sass.sms.enumeration;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public enum BusinessReminderType {


    /**
     * 5.1.1 延时通知的短信 (咨询通知)
     * 短信推送频率：咨询超过多久 只发一条。 在短信的样式里面。写清楚
     * 此短信使用功能：
     * 1. 在线咨询，医生医助发送了信息，患者没有回复
     */
    PATIENT_TIME_OUT_CONSULTATION_NOTICE("患者延时咨询通知"),




    PATIENT_DRUG_PURCHASE_WARNING("患者购药预警"),

    /**
     *
     *
     * 5.1.2 一日一次通知短信 (待办类)
     *       使用说明：今日存在待办，并系统开启了推送。发送一条短信，短信中设置患者端链接，患者通过链接打开进入到今日待办页面。查阅今日的待办内容。
     *     短信推送频率：早上发一条
     *     此短信使用功能：
     *       1. 用药提醒 （默认开启推送）
     *       2. 复查提醒
     *       3. 健康日志提醒
     *       4. 学习计划提醒
     *       5. 指标监测提醒
     *       6. 血压血糖提醒
     *       7. 自定义随访提醒
     *
     */
    PATIENT_TODAY_TO_DO_LIST("患者今日待办"),


    /**
     * 5.1.3 实时发送类短信 (系统通知类)
     *     使用说明：业务开启后，需要对用户立马下发通知。以防用户错过使用时机。
     *     短信推送频率： 立马发送
     *     此短信使用的功能：
     *       1. 病例讨论开始通知
     *       2. 转诊通知
     *       3. 在线咨询中医生医助 @患者 后的通知
     *
     *
     */
    PATIENT_NOTICE_OF_THE_START_CASE_DISCUSSION("患者病例讨论开始通知"),

    PATIENT_REFERRAL_NOTICE("患者转诊通知"),

    PATIENT_IMMEDIATELY_CONSULTATION_NOTICE("患者@咨询通知"),

    /**
     * 信息完整度。实时推送
     */
    PATIENT_INTEGRITY("患者信息完整度"),


    /**
     * 5.2.1 一日两次通知短信 （咨询通知）
     *       1. 患者发起的咨询消息(一天中午一条，晚上一条)
     */
    DOCTOR_TIME_OUT_CONSULTATION_NOTICE("医生延时咨询通知"),


    /**
     * 5.2.2 今日待办类 (待办类)
     *       1. 短信推送频率：（早上8点发送）
     *       2. 此短信使用的场景：
     *         1. 存在预约待审核
     */
    DOCTOR_TODAY_TO_DO_LIST("医生今日待办-预约审核"),


    /**
     * 5.2.3 实时发送类短信 (系统通知类)
     *     短信推送频率： 立马发送
     *     此短信使用的功能：
     *       1. 病例讨论开始通知
     *       2. 病例讨论邀请通知
     */
    @Deprecated
    DOCTOR_NOTICE_OF_THE_START_CASE_DISCUSSION("医生病例讨论开始通知"),


    DOCTOR_NOTICE_OF_INVITE_CASE_DISCUSSION("医生病例讨论邀请通知");


    String name;


    BusinessReminderType(String name) {

        this.name = name;
    }


    /**
     * 患者购药预警
     * 敏识燕语的马燕医生提醒您“氯雷他定片”即将用完，如需继续用药，请提前备好药品，点击链接查看详情
     * @return
     */
    public static String getPatientDrugPurchaseWarning(String tenantName, String doctorName, String role, String drugName) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
//        jsonObject.put("doctorName", doctorName + role);
        jsonObject.put("doctorName", doctorName);
        jsonObject.put("drugName", drugName);
        return jsonObject.toJSONString();

    }


    /**
     * 信息完整度
     * @return
     */
    public static String getPatientIntegrity(String tenantName, String doctorName, String roleName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
//        jsonObject.put("doctorName", doctorName + roleName);
        jsonObject.put("doctorName", doctorName);
        return jsonObject.toJSONString();
    }

    /**
     * 【敏瑞健康】“${name}”提醒您${smsSendUserName}回复了您的咨询消息，点击链接即可查看
     * @param tenantName
     * @param smsSendUserName
     * @return
     */
    public static String getPatientTimeOutConsultationNotice(String tenantName, String smsSendUserName, String roleName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
//        jsonObject.put("smsSendUserName", smsSendUserName + roleName);
        jsonObject.put("smsSendUserName", smsSendUserName);
        return jsonObject.toJSONString();

    }


    /**
     * 【敏瑞健康】“${name}”提醒您${smsSendUserName}回复了您的咨询消息，点击链接即可查看
     * @param tenantName
     * @return
     */
    public static String getPatientImmediatelyConsultationNotice(String tenantName, String smsSendUserName, String userRole) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
//        jsonObject.put("smsSendUserName", smsSendUserName + userRole);
        jsonObject.put("smsSendUserName", smsSendUserName);
        return jsonObject.toJSONString();

    }


    /**
     * 【敏瑞健康】“${name}”提醒您${formName}已将您转至耳鼻喉科${toName}名下，转诊码点击链接可获取
     */
    public static String getPatientReferralNotice(String tenantName, String formName, String formRole, String department, String toName, String toRole) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
        jsonObject.put("formName", formName + formRole);
        jsonObject.put("toName", StrUtil.isNotEmpty(department) ? department + toName + toRole : toName + toRole);
        return jsonObject.toJSONString();
    }


    /**
     * 根据短信模版生成 短信使用的参数
     * ${name} 提醒您今日${code}个待办事项，分别为${taskListName}，点击链接即可查看https://api.xxxx/api/msgs/${smsId}
     * @param tenantName
     * @param taskNumber
     * @param taskNameList
     * @return
     */
    public static String getPatientTodayToDoListSmsParams(String tenantName, Integer taskNumber, List<String> taskNameList) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
        jsonObject.put("code", taskNumber);
        String joined = String.join(",", taskNameList);
        if (joined.length() > 32) {
            joined = joined.substring(0, 31);
            joined += "...";
        }
        jsonObject.put("taskListName", joined);
        return jsonObject.toJSONString();

    }

    /**
     * 医生病例讨论邀请通知
     【敏瑞健康】“#{name}”提醒您${invitePeopleName}向您发起了${patientName}的病例讨论的邀请，点击链接可查看详情https://api.xxxx/api/msgs/${smsId}
     *
     * @param tenantName
     * @param invitePeopleName
     * @return
     */
    public static String getDoctorNoticeOfInviteCaseDiscussion(String tenantName, String invitePeopleName, String invitePeopleRole,
                                                               String patientName, String patientRole) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
//        jsonObject.put("invitePeopleName", invitePeopleName + invitePeopleRole);
        jsonObject.put("invitePeopleName", invitePeopleName);
//        jsonObject.put("patientName", patientName + patientRole);
        jsonObject.put("patientName", patientName);
        return jsonObject.toJSONString();
    }


    /**
     * 【敏瑞健康】“${name}”提醒您${peopleName}发起的病历讨论已经开始，点击链接进入讨论组
     * 患者病例讨论开始通知
     * @return
     */
    public static String getPatientNoticeOfTheStartCaseDiscussion(String tenantName, String invitePeopleName, String invitePeopleRole) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
        jsonObject.put("peopleName", invitePeopleName);
//        jsonObject.put("peopleName", invitePeopleName + invitePeopleRole);
        return jsonObject.toJSONString();
    }


    /**
     * 医生延时咨询通知
     * 【敏瑞健康】“${name}”提醒您，您有${number}位${roleName}向你发起了咨询，点击链接可回复
     * @param tenantName
     * @param userRole
     * @return
     */
    public static String getDoctorTimeOutConsultationNotice(String tenantName, Integer number, String userRole) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
        jsonObject.put("number", number);
        jsonObject.put("roleName", userRole);
//        jsonObject.put("roleName", "粉丝");
        return jsonObject.toJSONString();
    }

    /**
     * 【敏瑞健康】“${name}”提醒您有${number}个预约挂号需您审核，点击链接即可处理
     * @param tenantName
     * @param number
     * @return
     */
    public static String getDoctorTODOList(String tenantName, Integer number) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", tenantName);
        jsonObject.put("number", number);
        return jsonObject.toJSONString();
    }

}
