package com.caring.sass.wx.dto.template;

import com.caring.sass.utils.DateUtils;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @className: CommonTemplateServiceWorkModel
 * @author: 杨帅
 * @date: 2023/7/25
 */
public class CommonTemplateServiceWorkModel {


    public static String DRUGS_RESULT_INFORMATION = "DRUGS_RESULT_INFORMATION";

    public static String REFERRAL_CARD = "REFERRAL_CARD";

    public static String INFORMATION_IMPROVEMENT = "INFORMATION_IMPROVEMENT";

    public static String MEDICATION_WARNING_REMINDER = "MEDICATION_WARNING_REMINDER";

    public static String MESSAGE_REMINDER;

    public static String MEDICATION_REMINDER = "MEDICATION_REMINDER";

    public static String APPOINTMENT_RESULT_NOTIFICATION = "APPOINTMENT_RESULT_NOTIFICATION";

    public static String APPOINTMENT_AUDIT_NOTIFICATION = "APPOINTMENT_AUDIT_NOTIFICATION";

    public static String MONITORING_PLAN = "MONITORING_PLAN";

    public static String FOLLOW_UP_REMINDER = "FOLLOW_UP_REMINDER";

    public static String REGISTRATION_REMINDER = "注册提醒";

    public static String HEALTH_SERVICE_CLAIM_NOTIFICATION = "健康服务领取通知";


    public static List<WxMpTemplateData> buildWxMpTemplateData(String name, String serviceName) {

        List<WxMpTemplateData> data = new ArrayList<>();
        data.add(new WxMpTemplateData("thing3", name));
        data.add(new WxMpTemplateData("thing4", serviceName));
        data.add(new WxMpTemplateData("time10", DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)));
        return data;

    }

}
