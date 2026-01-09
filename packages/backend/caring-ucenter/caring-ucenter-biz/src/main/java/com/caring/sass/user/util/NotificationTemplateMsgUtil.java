package com.caring.sass.user.util;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.utils.DateUtils;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName NotificationTemplateMsgUtil
 * @Description
 * @Author yangShuai
 * @Date 2021/11/4 11:34
 * @Version 1.0
 */
public class NotificationTemplateMsgUtil {


    /**
     * 根据 appId, msgDtoRData, sendRecord, 生成推送微信模板需要的 实体
     */
    public static SendTemplateMessageForm create(String appId, String url, String openId,
                                                 String userName, TemplateMsgDto msgDtoRData) {

        SendTemplateMessageForm form = new SendTemplateMessageForm();
        form.setWxAppId(appId);
        WxMpTemplateMessage message = new WxMpTemplateMessage();
        List<TemplateMsgFields> fields = msgDtoRData.getFields();
        if (CollUtil.isEmpty(fields)) {
            return null;
        }
        if (StringUtils.isEmpty(msgDtoRData.getTemplateId())) {
            return null;
        }
        message.setTemplateId(msgDtoRData.getTemplateId());
        message.setToUser(openId);
        message.setUrl(url);

        message.setData(getData(fields, userName));

        form.setTemplateMessage(message);
        return form;
    }

    private static List<WxMpTemplateData> getData(List<TemplateMsgFields> fields, String userName) {
        List<WxMpTemplateData> wxMpTemplateData = new ArrayList<>(fields.size());
        WxMpTemplateData data;
        for (TemplateMsgFields field : fields) {
            String value = getValue(field, userName);
            data = new WxMpTemplateData(field.getAttr(), value, field.getColor());
            wxMpTemplateData.add(data);
        }
        return wxMpTemplateData;
    }

    private static String getValue(TemplateMsgFields field, String userName) {
        String value = field.getValue();
        if (value.contains("${name}")) {
            value = value.replace("${name}", userName);
        }
        if (value.contains("${time}")) {
            value = value.replace("${time}", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        }
        return value;
    }

}
