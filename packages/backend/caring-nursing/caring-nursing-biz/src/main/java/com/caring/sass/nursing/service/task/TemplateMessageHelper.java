package com.caring.sass.nursing.service.task;

import com.caring.sass.base.R;
import com.caring.sass.common.utils.paramtericText.ParametricTextManager;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class TemplateMessageHelper {


    public static TemplateMsgDto getTemplateMsgDto(String indefiner) {
        TemplateMsgApi templateMsgApi = SpringUtils.getBean(TemplateMsgApi.class);
        try {
            R<TemplateMsgDto> msgApiByIndefiner = templateMsgApi.getCommonCategoryServiceWorkOrderMsg(BaseContextHandler.getTenant(), null, indefiner);
            if (msgApiByIndefiner.getIsSuccess() && msgApiByIndefiner.getData() != null) {
                return msgApiByIndefiner.getData();
            }
        } catch (Exception e) {
            log.error("获取模板消息异常", e);
        }
        return null;
    }


    public static WxMpTemplateMessage setField(TemplateMsgDto templateMsgDto, Map<String, Object> params) {
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        if (templateMsgDto != null) {
            ParametricTextManager parametricTextManager = SpringUtils.getBean(ParametricTextManager.class);
            List<WxMpTemplateData> dataList = new ArrayList();
            List<TemplateMsgFields> msgDtoFields = templateMsgDto.getFields();
            for (Map.Entry entry : params.entrySet()) {
                parametricTextManager.addParameter((String) entry.getKey(), entry.getValue());
            }
            for (TemplateMsgFields fields : msgDtoFields) {
                if (fields.getValue() != null && fields.getValue().startsWith("${")) {
                    String str = parametricTextManager.format(fields.getValue());
                    dataList.add(new WxMpTemplateData(fields.getAttr(), str, fields.getColor()));
                } else {
                    dataList.add(new WxMpTemplateData(fields.getAttr(), fields.getValue(), fields.getColor()));
                }
            }
            wxMpTemplateMessage.setData(dataList);
            ParametricTextManager.remove();
            return wxMpTemplateMessage;
        }
        return null;
    }


}
