package com.caring.sass.common.constant;

/**
 * 用户类型常量
 *
 * @author xinzh
 */
public interface UserType {
    /**
     * 会员OR患者
     */
    String PATIENT = "PATIENT";
    /**
     * 项目超管
     */
    String ADMIN = "ADMIN";

    /**
     * 机构管理员
     */
    String ORGAN_ADMIN = "ORGAN_ADMIN";
    /**
     * 医生
     */
    String DOCTOR = "DOCTOR";

    /**
     * 小程序用户
     */
    String MiniAppUSER = "MINIAPPUSER";

    /**
     * AI创作用户
     */
    String AI_ARTICLE_USER = "AI_ARTICLE_USER";

    /**
     * sass全局超管
     */
    String GLOBAL_ADMIN = "GLOBAL_ADMIN";

    /**
     * 也可能是CMS管理员
     */
    String CMS_ADMIN = "CMS_ADMIN";

    /**
     * 第三方客户代理商
     */
    String THIRD_PARTY_CUSTOMERS = "THIRD_PARTY_CUSTOMERS";


    /**
     * 项目统计运维管理员
     */
    String ADMIN_OPERATION = "admin_operation";


    /**
     * 护理专员
     */
    String NURSING_STAFF = "NURSING_STAFF";



    /**
     * Ucenter服务中的 护理专员
     */
    String UCENTER_NURSING_STAFF = "NursingStaff";
    /**
     * Ucenter服务中医生
     */
    String UCENTER_DOCTOR = "doctor";

    /**
     * Ucenter服务中 小组
     */
    String UCENTER_group = "group";

    /**
     * 医生所在小组中的医生id
     */
    String UCENTER_DOCTOR_GROUP = "UCENTER_DOCTOR_GROUP";

    /**
     * Ucenter服务中 病人or 会员
     */
    String UCENTER_PATIENT = "patient";

    /**
     * 系统IM 。充当关键字回复时的人员角色
     */
    String UCENTER_SYSTEM_IM = "system_im";
    /**
     * 会诊小组的来宾
     */
    String CONSULTATION_GUEST = "CONSULTATION_GUEST";

    /**
     * 游客
     */
    String TOURISTS = "saas_tourists";

}
