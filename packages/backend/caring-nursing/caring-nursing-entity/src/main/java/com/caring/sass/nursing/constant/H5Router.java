package com.caring.sass.nursing.constant;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.util.PlanDict;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * h5路由地址
 *
 * @author xinzh
 */
public class H5Router {

    /**
     * 基础信息
     */
    public static String BASE_INFO_INDEX = "baseinfo/index";


    /**
     * 基础信息编辑
     */
    public static String BASE_INFO_EDIT = "baseinfo/editor";


    /**
     * 监测数据
     */
    public static String MONITOR_INDEX = "monitor/index";

    /**
     * 血压监测
     */
    public static String MONITOR_PRESSURE = "monitor/pressure";


    /**
     * 血压监测编辑
     */
    public static String MONITOR_PRESSURE_EDITOR = "monitor/pressureEditor";


    /**
     * 血糖监测主页
     */
    public static String MONITOR_GLUCOSE = "monitor/glucose";

    /**
     * 血糖监测编辑
     */
    public static String MONITOR_GLUCOSE_EDITOR = "monitor/glucoseEditor";

    /**
     * 用药日历
     */
    public static String CALENDAR_INDEX = "calendar/index";


    /**
     * 智能提醒
     */
    public static String REMIND_INDEX = "remind/index";


    /**
     * 我的药箱
     */
    public static String MEDICINE_INDEX = "medicine/index";

    /**
     * 检测数据
     */
    public static String TEST_NUMBER_SHOW_DATA = "testNumber/editor";

    /**
     * 健康日志
     */
    public static String HEALTH_CALENDAR_INDEX = "healthCalendar/index";

    /**
     * 健康日志详情
     */
    public static String HEALTH_CALENDAR_SHOW_DATA = "healthCalendar/showdata";

    /**
     * 健康日志编辑
     */
    public static String HEALTH_CALENDAR_EDITOR = "healthCalendar/editor";

    /**
     * 疾病信息
     */
    public static String HEALTH_INDEX = "health/index";


    /**
     * 疾病信息编辑
     */
    public static String HEALTH_EDITOR = "health/editor";

    /**
     * cms首页
     */
    public static String CMS_INDEX = "cms/index";

    /**
     * cms文章详情
     */
    public static String CMS_DETAIL = "cms/show";

    /**
     * apk扫码下载地址
     */
    public static String APK_SCAN_DOWNLOAD = "loadapp/index";

    /**
     * UNIapk扫码下载地址
     */
    public static String UNI_APK_SCAN_DOWNLOAD = "loadapp/index2";

    /**
     * 患者入组地址
     */
    public static String PATIENT_BASE_URL = "wx";

    /**
     * 医生根路径
     */
    public static String DOCTOR_BASE_URL = "doctor";

    /**
     * 聊天页面
     */
    public static String CHAT = "im/index";

    /**
     * 会诊通知
     */
    public static String CONSULTATION_NOTICE = "consultation/%d";

    /**
     * 病历讨论邀请
     */
    public static String CONSULTATION_INVITE = "consultation/index/invite";

    /**
     * 转诊卡
     */
    public static String REFERRAL_CARD_URL = "scanningReferral/index?id=%d";

    /**
     * 购药提醒URL
     */
    public static String BUY_DRUGS_URL = "medicine/medicineReminder?drugId=%d";

    /**
     * 转诊卡二维码内容
     */
    public static String DOCTOR_REFERRAL_CARD_URL = "scanningReferral/index?id=%d&acceptDoctorId=%d";

    /**
     * 医生入口
     */
    public static String CONSULTATION_DOCTOR_NOTICE = "consultation/index/%d";

    /**
     * 医患互动
     */
    public static String DOCTOR_CHAT = "im/messageList";

    /**
     * 医患互动 直接进入某个患者的聊天页面
     * 患者的IM账号和患者id
     */
    public static String DOCTOR_PATIENT_CHAT = "im/index?imAccount=%s&imPatientId=%d&formPage=weiXinTemplateMessage";

    /**
     * 医助和患者的聊天页面
     */
    public static String NURSING_PATIENT_CHAT = "/#/pages/chatroom/chatroom?patientId=%d&fromPath=weiXinTemplateMessage";


    /**
     * 自定义护理计划的表单的填写页面
     */
    public static String CUSTOM_FOLLOW_UP_ADD = "custom/follow/%d/editor/%d";


    /**
     * 自定义随访只添加，不回显。
     */
    public static String CUSTOM_FOLLOW_UP_ADD_NO_MESSAGE = "custom/follow/%d/editor";

    /**
     * 自定义监测计划的表单填写页面
     */
    public static String CUSTOM_MONITOR_DATA_ADD = "monitor/add?id=%d&messageId=%d&title=%s";
    /**
     * 患者推荐页链接地址
     */
    public static String RECOMMEND_URL = "mine/invitation";

    /**
     * 患者 我的预约
     */
    public static String PATIENT_RESERVATION = "reservation/myreservation";

    /**
     * 患者预约失败
     */
    public static String PATIENT_RESERVATION_FAILED = "reservation/appointmentFailed";

    /**
     * 医生的待审核页面
     */
    public static String DOCTOR_RESERVATION_APPROVE = "reservation/reviewedList";

    /**
     * 患者没有找到。
     */
    public static String PATIENT_NOT_FIND = "wxauthorize/refuse";

    /**
     * 患者随访信息完善
     */
    public static String PATIENT_INTEGRITY = "integrity";

    /**
     * 游客进入时。点此注册的菜单路径。
     */
    public static String H5_RULE_SELECT = "rule/select";

    private static final Map<PlanEnum, String> URL_HOLDER = new HashMap<>();

    static {
        URL_HOLDER.put(PlanEnum.BLOOD_PRESSURE, MONITOR_PRESSURE_EDITOR);
        URL_HOLDER.put(PlanEnum.BLOOD_SUGAR, MONITOR_GLUCOSE_EDITOR);
        URL_HOLDER.put(PlanEnum.REVIEW_REMIND, TEST_NUMBER_SHOW_DATA);
        URL_HOLDER.put(PlanEnum.HEALTH_LOG, HEALTH_CALENDAR_EDITOR);
        URL_HOLDER.put(PlanEnum.LEARN_PLAN, CMS_INDEX);
        URL_HOLDER.put(PlanEnum.CUSTOM_PLAN, CUSTOM_FOLLOW_UP_ADD);
        URL_HOLDER.put(PlanEnum.NUTRIYIOUS_DIET, "");
    }

    public static String timeOutFormAddUrl() {
        return CUSTOM_FOLLOW_UP_ADD_NO_MESSAGE;
    }

    public static String getRouterByPlanEnum(PlanEnum planEnum, String followUpPlanType) {

        /**
         * 计划类型是null 表示是自定义。
         * 判断计划是护理计划还是监测计划
         */
        if (Objects.isNull(planEnum.getCode())) {
            if (PlanDict.MONITORING_DATA.equals(followUpPlanType)) {
                return CUSTOM_MONITOR_DATA_ADD;
            } else {
                return CUSTOM_FOLLOW_UP_ADD;
            }
        }
        String router = URL_HOLDER.get(planEnum);
        if (StrUtil.isBlank(router)) {
            return TEST_NUMBER_SHOW_DATA;
        }
        return router;
    }
}
