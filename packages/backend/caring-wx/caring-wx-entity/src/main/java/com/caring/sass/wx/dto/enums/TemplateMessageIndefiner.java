package com.caring.sass.wx.dto.enums;

/**
 * 微信模板消息关键字
 *
 * @author xinzh
 */
public interface TemplateMessageIndefiner {

    /**
     * 待审核预约
     */
    String RESERVATION_UPDATE_TEMPLATE_MESSAGE = "ReservationUpdateTemplateMessage";
    /**
     * 入组提醒
     */
    String ENTRY_GROUP_ALERT_TEMPLATE_MESSAGE = "EntryGroupAlertTemplateMessage";
    /**
     * 用药打卡
     */
    String MEDICINE_TAKE_ALERT_TEMPLATE_MESSAGE = "MedicineTakeAlertTemplateMessage";
    /**
     * 医生未读消息
     */
    String UN_READ_MESSAGE_ALERT_TEMPLATE_MESSAGE = "UnReadMessageAlertTemplateMessage";
    /**
     * 助理未读消息
     * TODO: 此参数暂时作废
     */
    String COMMISSIONER_MESSAGE_UN_READ_TEMPLATE_MESSAGE = "CommissionerMessageUnReadTemplateMessage";
    /**
     * 护理计划
     */
    String NURSING_PLAN = "NursingPlan";
    /**
     * 预约结果通知
     */
    String RESERVATION_TEMPLATE_MESSAGE = "ReservationTemplateMessage";
    /**
     * 咨询回复
     */
    String CONSULTATION_RESPONSE = "ConsultationResponse";
    /**
     * 咨询回复 （患者）
     * 患者 发送出来的消息 推送给医生的模板
     */
    String CONSULTATION_RESPONSE_PATIENT = "ConsultationResponsePatient";

    /**
     * 咨询回复 (医生医助)
     * 医助 医生 发送的消息 ，推送给医生的模板消息
     */

    String CONSULTATION_RESPONSE_NURSING = "ConsultationResponseNursing";

    /**
     * 会诊通知
      */
    String CONSULTATION_NOTICE = "CONSULTATION_NOTICE";


    String DOCTOR_AUDIT_NOTICE = "DOCTOR_AUDIT_NOTICE";

    /**
     * 会诊消息通知
     */
    String CONSULTATION_PROCESSING = "CONSULTATION_PROCESSING";
    /**
     * 会诊结束通知
     */
    String CONSULTATION_END = "CONSULTATION_END";

    /** 转诊卡 */
    String REFERRAL_CARD = "REFERRAL_CARD";

    /**
     * 购药提醒
     */
    String BUY_DRUGS_REMINDER = "BUY_DRUGS_REMINDER";

    /**
     * 群发通知
     */
    String MASS_MAILING_NOTIFY = "Mass_Mailing_notify";
    /**
     * 信息完整度
     */
    String COMPLETENESS_INFORMATION = "COMPLETENESS_INFORMATION";

    /**
     * 通过类目模板
     * 服务工单已推送提醒
     * 姓名 张一
     * 服务类型  医疗
     * 日期  2023-07-11
     */
    String COMMON_CATEGORY_SERVICE_WORK_ORDER = "COMMON_CATEGORY_SERVICE_WORK_ORDER";


    /**
     * 医生评论提醒模版
     *服务工单已接收提醒
     * 服务日期  2023-12-09
     * 姓名   张三
     * 服务类型  医疗
     */
    String DOCTOR_COMMENT_REMINDER = "DOCTOR_COMMENT_REMINDER";

}
