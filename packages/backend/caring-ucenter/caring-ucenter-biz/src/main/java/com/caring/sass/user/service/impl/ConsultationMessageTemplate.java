package com.caring.sass.user.service.impl;

import com.caring.sass.utils.DateUtils;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import com.google.common.collect.ImmutableMap;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ConsultationMessageTemplate
 * @Description
 * @Author yangShuai
 * @Date 2021/6/9 14:52
 * @Version 1.0
 */
public class ConsultationMessageTemplate {

    static Map<String, Params> consultationForReaction = ImmutableMap.<String, Params>builder()
            .put(TemplateMessageIndefiner.CONSULTATION_NOTICE, new ConsultationNoticeParams())
            .put(TemplateMessageIndefiner.CONSULTATION_PROCESSING, new ConsultationProcessingParams())
            .put(TemplateMessageIndefiner.CONSULTATION_END, new ConsultationEndParams())
            .build();

    public interface Params {

        String FIRST = "first";
        String KEYWORD_1 = "keyword1";
        String KEYWORD_2 = "keyword2";
        String REMARK = "remark";

        /**
         * 根据consultationName 初始化一个模板参数
         * @param consultationName
         * @return
         */
        List<WxMpTemplateData> init(String consultationName, String userName, Boolean commonCategory);
    }

    /**
     * 设置讨论通知的模板
     */
    public static class ConsultationNoticeParams implements Params{

        @Override
        public List<WxMpTemplateData> init(String consultationName, String userName, Boolean commonCategory) {
            if (commonCategory) {
                return CommonTemplateServiceWorkModel.buildWxMpTemplateData(userName, "病例讨论开始");
            }
            List<WxMpTemplateData> objects = new ArrayList<>();
            objects.add(new WxMpTemplateData(FIRST, "您的讨论小组已创建，等待您的进入"));
            objects.add(new WxMpTemplateData(KEYWORD_1, consultationName));
            objects.add(new WxMpTemplateData(KEYWORD_2, "已开启"));
            objects.add(new WxMpTemplateData(REMARK, "点击进入，进行在线交流 >"));
            return objects;
        }
    }


    /**
     * 邀请的通知
     */
    public static class ConsultationInviteParams implements Params{

        @Override
        public List<WxMpTemplateData> init(String consultationName, String userName, Boolean commonCategory) {
            if (commonCategory) {
                return CommonTemplateServiceWorkModel.buildWxMpTemplateData(userName, "病例讨论邀请通知");
            }
            List<WxMpTemplateData> objects = new ArrayList<>();
            objects.add(new WxMpTemplateData(FIRST, "收到一个新的讨论小组邀请，请及时处理"));
            objects.add(new WxMpTemplateData(KEYWORD_1, consultationName));
            String date = DateUtils.format(new Date(), "yyyy.MM.dd HH:mm");
            objects.add(new WxMpTemplateData(KEYWORD_2, date));
            objects.add(new WxMpTemplateData(REMARK, "点此查看详情"));
            return objects;
        }
    }

    /**
     * 设置讨论进行中消息通知模板
     */
    public static class ConsultationProcessingParams implements Params{

        @Override
        public List<WxMpTemplateData> init(String consultationName,  String userName, Boolean commonCategory) {
            if (commonCategory) {
                return CommonTemplateServiceWorkModel.buildWxMpTemplateData(userName, "病例讨论消息通知");
            }
            List<WxMpTemplateData> objects = new ArrayList<>();
            objects.add(new WxMpTemplateData(FIRST, "您的讨论小组收到一条讨论提醒"));
            objects.add(new WxMpTemplateData(KEYWORD_1, consultationName));
            objects.add(new WxMpTemplateData(KEYWORD_2, "进行中"));
            objects.add(new WxMpTemplateData(REMARK, "点击进入讨论小组，可进行在线交流 >"));
            return objects;
        }

    }

    /**
     * 设置讨论结束时模板通知
     */
    public static class ConsultationEndParams implements Params {

        @Override
        public List<WxMpTemplateData> init(String consultationName,  String userName, Boolean commonCategory) {
            if (commonCategory) {
                return CommonTemplateServiceWorkModel.buildWxMpTemplateData(userName, "病例讨论结束通知");
            }
            List<WxMpTemplateData> objects = new ArrayList<>();
            objects.add(new WxMpTemplateData(FIRST, "您的讨论小组已解散"));
            objects.add(new WxMpTemplateData(KEYWORD_1, consultationName));
            objects.add(new WxMpTemplateData(KEYWORD_2, "已结束"));
            objects.add(new WxMpTemplateData(REMARK, "如有疑问，可直接与您的医生或医助联系"));
            return objects;
        }

    }












}
