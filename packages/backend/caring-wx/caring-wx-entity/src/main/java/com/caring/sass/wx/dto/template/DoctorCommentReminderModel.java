package com.caring.sass.wx.dto.template;

import com.caring.sass.utils.DateUtils;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 医生 对待办 消息进行评论后。发送给患者的模板
 */
public class DoctorCommentReminderModel {


    /**
     * 医生 对待办 消息进行评论后。发送给患者的模板
     * @param userName
     * @param remark
     * @return
     */
    public static List<WxMpTemplateData> buildWxMpTemplateData(String userName, String remark) {

        List<WxMpTemplateData> data = new ArrayList<>();
        data.add(new WxMpTemplateData("time13", DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)));
        data.add(new WxMpTemplateData("thing3", userName));
        data.add(new WxMpTemplateData("thing4", remark));
        return data;

    }

}
