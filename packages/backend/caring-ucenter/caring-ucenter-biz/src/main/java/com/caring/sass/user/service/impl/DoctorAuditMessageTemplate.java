package com.caring.sass.user.service.impl;

import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.google.common.collect.ImmutableMap;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ConsultationMessageTemplate
 * @Description
 * @Author yangShuai
 * @Date 2021/6/9 14:52
 * @Version 1.0
 */
public class DoctorAuditMessageTemplate {

    static Map<String, Params> consultationForReaction = ImmutableMap.<String, Params>builder()
            .put(TemplateMessageIndefiner.DOCTOR_AUDIT_NOTICE, new DoctorAuditParams())
            .build();

    public interface Params {

        String FIRST = "first";
        String KEYWORD_1 = "keyword1";
        String KEYWORD_2 = "keyword2";
        String REMARK = "remark";

        /**
         * 根据consultationName 初始化一个模板参数
         * @return
         */
        List<WxMpTemplateData> init();
    }

    /**
     * 设置讨论通知的模板
     */
    public static class DoctorAuditParams implements Params{

        @Override
        public List<WxMpTemplateData> init() {
            LocalDateTime dateTime = LocalDateTime.now();
            LocalDate localDate = dateTime.toLocalDate();
            LocalTime localTime = dateTime.toLocalTime();

            List<WxMpTemplateData> objects = new ArrayList<>();
            objects.add(new WxMpTemplateData(FIRST, "您收到新的消息"));
            objects.add(new WxMpTemplateData(KEYWORD_1, "您的医生申请已审核通过。"));
            objects.add(new WxMpTemplateData(KEYWORD_2,
                    localDate.toString() + " " + localTime.toString().substring(0, 5)));
            objects.add(new WxMpTemplateData(REMARK, ""));
            return objects;
        }
    }

}
