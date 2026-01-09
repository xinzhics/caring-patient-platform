package com.caring.sass.common.redis;

/**
 * @ClassName SaasRedisBusinessKey
 * @Description
 * @Author yangShuai
 * @Date 2022/4/26 14:11
 * @Version 1.0
 **/
public class SaasRedisBusinessKey {

    /**
     * 项目的租户字典
     */
    public static String TENANT_DICTIONARY = "tenant_dictionary:";

    public static String TENANT_LOGIN = "tenant_login";

    /**
     * 项目的数据安全开关
     */
    public static String TENANT_DATA_SECURITY_SETTINGS = "tenant_data_security_settings";

    /**
     * 项目服务号类型
     */
    public static String TENANT_OFFICIAL_ACCOUNT_TYPE = "tenant_official_account_type";

    /**
     * 患者信息完整度管理历史 计数问题
     */
    public static String PATIENT_INFO_MANAGEMENT_HISTORY = "patient_info_management_history";

    /**
     * 患者删除事件
     */
    private static String PATIENT_DELETE_KEY = "tenant:patient_delete_event";

    /**
     * 患者更换医生事件
     */
    public static String PATIENT_CHANGE_DOCTOR = "tenant:patient_change_doctor";

    /**
     * 医生更换医助
     */
    public static String DOCTOR_CHANGE_NURSING = "tenant:doctor_change_nursing";

    /**
     * 医生更换医助的服务锁
     */
    private static String DOCTOR_CHANGE_NURSING_HANDLE_KEY = "tenant:%s_Doctor_%d_change_nursing_handle_%d";

    /**
     * 医助转移数据到新医助
     */
    private static String NURSING_CHANGE_HANDLE_KEY = "tenant:%s_nursing_%d_change_%d_handle";


    /**
     * 医助转移数据
     */
    public static String NURSING_CHANGE = "tenant:nursing_change";

    /**
     * 患者删除handle
     * %s 中填充服务名称
     * 如： nursing服务处理患者删除需要填充为 tenant:patient_delete_nursing_handle
     */
    private static String PATIENT_DELETE_HANDLE_KEY = "tenant:patient_delete_%s_handle_%s";

    /**
     * 系统 游客 用户默认角色
     */
    private static String REDIS_KEY_WX_USER_DEFAULT_ROLE = "tenant:wx_user_default_role";


    /**
     * 项目预约医生范围控制。
     */
    private static String TENANT_APPOINTMENT_DOCTOR_SCOPE = "tenant:appointmentDoctorScope";

    /**
     * 项目IOS的版本。
     */
    private static String TENANT_IOS_VERSION = "tenant:ios_version";

    /**
     * 信息完整的同步任务开关。
     */
    private static String TENANT_CODE_INFORMATION_INTEGRITY_SWITCH = "tenant:code_information_integrity_switch";

    /**
     * 患者管理-监测数据-监测计划-监测指标的同步任务开关。
     */
    private static String TENANT_CODE_INDICATORS_CONDITION_SWITCH = "tenant:code_indicators_condition_switch";
    /**
     * 患者管理-用药预警-预警药品及条件的同步任务开关。
     */
    private static String TENANT_CODE_DRUGS_CONDITION_SWITCH = "tenant:code_drugs_condition_switch";

    /**
     * 患者管理-选择跟踪任务开关
     */
    private static String TENANT_TRACE_INTO_SWITCH = "tenant:trace_into_switch";

    /**
     * 患者管理-选择跟踪任务开关
     */
    public static String UN_FINISHED_REMIND = "tenant:un_finished_remind";

    /**
     * 患者管理-选项跟踪，表单结果处理锁
     */
    private static String TENANT_TRACE_INTO_RESULT = "trace_into_result:";

    /**
     * 患者管理-患者推荐关系-推荐人生成邀请二维码缓存。
     */
    private static String TENANT_CODE_PATIENT_INVITATION_QRCODE = "tenant:code_patient_invitation_qrcode";

    /**
     * 项目导出记录的序号
     */
    public static String TENANT_EXPORT_COUNT = "tenant_export_count";

    /**
     * 项目导出记录的记录。 若 redis 中 任务不存在。则导出任务终止
     */
    private static String TENANT_EXPORT_TASK_ID = "tenant_export_%s_task_id:%d";

    /**
     * 患者发送消息后的消息队列
     */
    public static String PATIENT_MESSAGE_KEYWORD_LIST = "patient_message_keyword_list";

    /**
     * 患者关注后。消息队列
     */
    public static String PATIENT_FOLLOW_UP_EVENT = "patient_follow_up_event";

    /**
     * 建站页面保存的key。 key 存在表示保存未完成，不能再次提交。
     */
    public static String CMS_SITE_BUILD_PAGE_SAVE = "CMS_SITE_BUILD_PAGE_SAVE:%d";

    /**
<<<<<<< HEAD
     * 系统所有的表单缓存的key前缀。后面跟租户。
     */
    public static String SYSTEM_FORM_REDIS_TENANT = "SYSTEM_FORM_REDIS_TENANT:";

    /**
     * cms内容
     */
    public static String CMS_CONTENT_ALL = "CMS_CONTENT_ALL:";

    /**
     * cms内容的点击量
     */
    public static String CMS_CONTENT_HIT_COUNT = "CMS_CONTENT_HIT_COUNT:";
    /*
     * 表单提交上限
     */
    public static String FORM_DAILY_SUBMISSION_LIMIT = "FORM_DAILY_SUBMISSION_LIMIT:";

    /**
     * 项目app配置的 启动页图片
     */
    public static String TENANT_APP_CONFIG_LAUNCH_IMAGE = "TENANT_APP_CONFIG_LAUNCH_IMAGE";

    /**
     * 小程序的用户密钥
     */
    public static String MINI_APP_USER_SESSION_KEY = "MINI_APP_USER_SESSION_KEY";


    public static String PODCAST_AUDIO_TASK_ID = "PODCAST_AUDIO_TASK_ID";

    /**
     * 推送模版消息任务队列
     */
    public static String PUSH_TEMPLATE_MESSAGE_TASK = "push_template_message_task";


    public static String getTenantExportTaskId(String tenantCode, Long exportTaskId) {
        return String.format(TENANT_EXPORT_TASK_ID, tenantCode, exportTaskId);
    }

    public static String getPatientDeleteKey() {
        return PATIENT_DELETE_KEY;
    }

    public static String getPatientDeleteHandleKey(String serverName, String patientId) {
        return String.format(PATIENT_DELETE_HANDLE_KEY, serverName, patientId);
    }


    public static String getNursingChangeHandleKey(String serverName, Long formNursingId, Long toNursingId) {
        return String.format(NURSING_CHANGE_HANDLE_KEY, serverName, formNursingId, toNursingId);
    }

    public static String getDoctorChangeNursingHandleKey(String serverName, Long doctorId, Long nursingId) {
        return String.format(DOCTOR_CHANGE_NURSING_HANDLE_KEY, serverName, doctorId, nursingId);
    }

    public static String getRedisKeyWxUserDefaultRole() {
        return REDIS_KEY_WX_USER_DEFAULT_ROLE;
    }

    public static String getTenantAppointmentDoctorScope() {
        return TENANT_APPOINTMENT_DOCTOR_SCOPE;
    }

    public static String getTenantCodeInformationIntegritySwitch() {
        return TENANT_CODE_INFORMATION_INTEGRITY_SWITCH;
    }

    public static String getTenantCodeIndicatorsConditionSwitch() {
        return TENANT_CODE_INDICATORS_CONDITION_SWITCH;
    }

    public static String getTenantCodeDrugsConditionSwitch() {
        return TENANT_CODE_DRUGS_CONDITION_SWITCH;
    }

    public static String getTenantTraceIntoSwitch() {
        return TENANT_TRACE_INTO_SWITCH;
    }

    public static String getTenantTraceIntoResult() {
        return TENANT_TRACE_INTO_RESULT;
    }

    public static String getTenantCodePatientInvitationQrcode() {
        return TENANT_CODE_PATIENT_INVITATION_QRCODE;
    }


    public static String getTenantIosVersion() {
        return TENANT_IOS_VERSION;
    }
}
