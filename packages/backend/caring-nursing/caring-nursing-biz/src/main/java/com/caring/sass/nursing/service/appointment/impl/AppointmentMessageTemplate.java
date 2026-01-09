package com.caring.sass.nursing.service.appointment.impl;

import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.constant.AppointmentStatusEnum;
import com.caring.sass.nursing.entity.appointment.Appointment;
import com.caring.sass.nursing.util.I18nUtils;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import com.google.common.collect.ImmutableMap;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @className: AppointmentMessageTemplate
 * @author: 杨帅
 * @date:  2023/3/28
 * @Desc: 预约模板
 */
public class AppointmentMessageTemplate {

    static Map<String, Message> appointmentTemplateMap = ImmutableMap.<String, Message>builder()
            // 待审核预约
            .put(TemplateMessageIndefiner.RESERVATION_UPDATE_TEMPLATE_MESSAGE, new AppointmentResultParams())
            // 预约结果通知
            .put(TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE, new AppointmentResultParams())
            .build();

    public interface Message {

        String FIRST = "first";
        String KEYWORD_1 = "keyword1";
        String KEYWORD_2 = "keyword2";
        String KEYWORD_3 = "keyword3";

        /**
         * 初始化患者收到的模板
         * @return
         */
        List<WxMpTemplateData> initPatient( Appointment appointment, boolean commonCategory, String defaultLanguage);

        /**
         * @title 发送给医生的模板
         * @author 杨帅 
         * @updateTime 2023/3/28 14:44 
         * @throws
         */
        List<WxMpTemplateData> initDoctor( Appointment appointment, boolean nursing, boolean commonCategory, String defaultLanguage);


        /**
         * 医生审批的模板
         * @param appointment
         * @return
         */
        List<WxMpTemplateData> doctorApprove( Appointment appointment, boolean commonCategory, String defaultLanguage);

    }


    /**
     * 预约结果模板
     */
    public static class AppointmentResultParams implements Message {

        /**
         * 初始化患者收到的 预约成功 或者预约失败的模板内容
         * @param appointment
         * @return
         */
        @Override
        public List<WxMpTemplateData> initPatient( Appointment appointment, boolean commonCategory, String defaultLanguage) {
            String patientName = appointment.getPatientName();
            // 预约 的时间
            LocalDate appointDate = appointment.getAppointDate();
            // 上午 下午
            Integer time = appointment.getTime();
            List<WxMpTemplateData> objects = new ArrayList<>();
            String key3Word = null;
            if (AppointmentStatusEnum.NO_VISIT.getCode() == appointment.getStatus()) {
                key3Word = I18nUtils.getMessageByTenantDefault("APPOINTMENT_SUCCESSFUL", defaultLanguage);
            } else if (AppointmentStatusEnum.AUDIT_FAILED.getCode() == appointment.getStatus()) {
                key3Word = I18nUtils.getMessageByTenantDefault("APPOINTMENT_FAILED", defaultLanguage);
            } else {
                key3Word = I18nUtils.getMessageByTenantDefault("APPOINTMENT_FAILED", defaultLanguage);
            }
            if (commonCategory) {
                String message = I18nUtils.getMessageByTenantDefault(CommonTemplateServiceWorkModel.APPOINTMENT_RESULT_NOTIFICATION, defaultLanguage);
                return CommonTemplateServiceWorkModel.buildWxMpTemplateData(patientName, key3Word);
            }

            // 医生的名字
            String doctorName = appointment.getDoctorName();
            String appointmentProject = String.format("%d月%d日%s%s ",appointDate.getMonthValue(), appointDate.getDayOfMonth(), time== 1? "上午" : "下午" , doctorName);

            objects.add(new WxMpTemplateData(KEYWORD_1, patientName));
            objects.add(new WxMpTemplateData(KEYWORD_2, appointmentProject));
            objects.add(new WxMpTemplateData(KEYWORD_3, key3Word));
            return objects;
        }


        @Override
        public List<WxMpTemplateData> initDoctor(Appointment appointment, boolean nursing, boolean commonCategory, String defaultLanguage) {

            String patientName = appointment.getPatientName();
            // 预约 的时间
            LocalDate appointDate = appointment.getAppointDate();
            Integer status = appointment.getStatus();
            String keyword_3_desc = "";
            if (AppointmentStatusEnum.NO_VISIT.getCode() == status) {
                keyword_3_desc = I18nUtils.getMessageByTenantDefault("APPOINTMENT_PASSED", defaultLanguage);
            } else if (AppointmentStatusEnum.AUDIT_FAILED.getCode() == status) {
                keyword_3_desc = I18nUtils.getMessageByTenantDefault("APPOINTMENT_rejected", defaultLanguage);
            } else if (AppointmentStatusEnum.VISIT_EXPIRED.getCode() == status) {
                keyword_3_desc = I18nUtils.getMessageByTenantDefault("APPOINTMENT_Failed_Expired", defaultLanguage);
            } else {
                throw new BizException("预约状态不需要发送模板");
            }
            if (commonCategory) {
                String message = I18nUtils.getMessageByTenantDefault(CommonTemplateServiceWorkModel.APPOINTMENT_RESULT_NOTIFICATION, defaultLanguage);
                return CommonTemplateServiceWorkModel.buildWxMpTemplateData(patientName, message + keyword_3_desc);
            }
            // 上午 下午
            Integer time = appointment.getTime();
            String appointmentProject = String.format("%d月%d日%s 就诊 ",appointDate.getMonthValue(), appointDate.getDayOfMonth(), time== 1? "上午" : "下午");

            if (nursing) {
                keyword_3_desc += "(已由医助处理)";
            }
            List<WxMpTemplateData> objects = new ArrayList<>();
            objects.add(new WxMpTemplateData(KEYWORD_1, patientName));
            objects.add(new WxMpTemplateData(KEYWORD_2, appointmentProject));
            objects.add(new WxMpTemplateData(KEYWORD_3, keyword_3_desc));
            return objects;
        }

        @Override
        public List<WxMpTemplateData> doctorApprove( Appointment appointment, boolean commonCategory, String defaultLanguage) {

            String patientName = appointment.getPatientName();
            LocalDate appointDate = appointment.getAppointDate();
            if (commonCategory) {
                return CommonTemplateServiceWorkModel.buildWxMpTemplateData(patientName, I18nUtils.getMessageByTenantDefault(CommonTemplateServiceWorkModel.APPOINTMENT_AUDIT_NOTIFICATION, defaultLanguage));
            }
            Integer time = appointment.getTime();
            String appointmentProject = String.format("%d月%d日%s",appointDate.getMonthValue(), appointDate.getDayOfMonth(), time== 1? "上午 就诊" : "下午 就诊");

            List<WxMpTemplateData> objects = new ArrayList<>();
            objects.add(new WxMpTemplateData(KEYWORD_1, patientName));
            objects.add(new WxMpTemplateData(KEYWORD_2, appointmentProject));
            objects.add(new WxMpTemplateData(KEYWORD_3, "待审核"));
            return objects;
        }

    }

}
