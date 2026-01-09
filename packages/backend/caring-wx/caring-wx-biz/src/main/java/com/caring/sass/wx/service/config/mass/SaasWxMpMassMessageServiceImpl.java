package com.caring.sass.wx.service.config.mass;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.common.utils.StringUtils;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpMassMessageServiceImpl;
import me.chanjar.weixin.mp.bean.*;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import me.chanjar.weixin.mp.bean.result.WxMpMassUploadResult;
import me.chanjar.weixin.mp.enums.WxMpApiUrl;

import java.util.List;

/**
 * @ClassName SaasWxMpMassMessageServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/11/22 15:30
 * @Version 1.0
 */
public class SaasWxMpMassMessageServiceImpl extends WxMpMassMessageServiceImpl {

    private WxMpService wxMpService;

    public SaasWxMpMassMessageServiceImpl(WxMpService wxMpService) {
        super(wxMpService);
        this.wxMpService = wxMpService;
    }

    /**
     * 上传图文到草稿箱
     * @param news
     * @return
     * @throws WxErrorException
     */
    @Override
    public WxMpMassUploadResult massNewsUpload(WxMpMassNews news) throws WxErrorException {
        String createDraft = "https://api.weixin.qq.com/cgi-bin/draft/add";
        String responseContent = wxMpService.post(createDraft, news.toJson());
        return WxMpMassUploadResult.fromJson(responseContent);
    }

    /**
     * 群发预览
     * @param wxMpMassPreviewMessage
     * @return
     * @throws WxErrorException
     */
    @Override
    public WxMpMassSendResult massMessagePreview(WxMpMassPreviewMessage wxMpMassPreviewMessage) throws WxErrorException {
        String responseContent = this.wxMpService.post(WxMpApiUrl.MassMessage.MESSAGE_MASS_PREVIEW_URL,
                SaasWxMpGsonBuilder.create().toJson(wxMpMassPreviewMessage));
        return WxMpMassSendResult.fromJson(responseContent);
    }


    /**
     * openIds 群发
     * @param message
     * @return
     * @throws WxErrorException
     */
    @Override
    public WxMpMassSendResult massOpenIdsMessageSend(WxMpMassOpenIdsMessage message) throws WxErrorException {
        List<String> toUsers = message.getToUsers();
        JSONObject postParams = getPostParams(toUsers, null, message.getMsgType(), message.getMediaId(),
                message.getContent(), message.isSendIgnoreReprint(), message.getClientMsgId());
        String responseContent = this.wxMpService.post(WxMpApiUrl.MassMessage.MESSAGE_MASS_SEND_URL, postParams.toJSONString());
        return WxMpMassSendResult.fromJson(responseContent);
    }

    /**
     * 分组群发
     * @param message
     * @return
     * @throws WxErrorException
     */
    @Override
    public WxMpMassSendResult massGroupMessageSend(WxMpMassTagMessage message) throws WxErrorException {
        JSONObject postParams = getPostParams(null, message.getTagId(), message.getMsgType(),
                message.getMediaId(), message.getContent(), message.isSendIgnoreReprint(), message.getClientMsgId());
        String responseContent = this.wxMpService.post(WxMpApiUrl.MassMessage.MESSAGE_MASS_SENDALL_URL, postParams.toJSONString());
        return WxMpMassSendResult.fromJson(responseContent);
    }

    private JSONObject getPostParams(List<String> toUsers, Long tagId,  String msgType, String mediaId,
                                     String content, boolean sendIgnoreReprint, String clientMsgId) {
        JSONObject postParam = new JSONObject();
        if (StringUtils.isNotEmptyString(clientMsgId)) {
            postParam.put("clientmsgid", clientMsgId);
        }
        JSONObject filter = new JSONObject();
        if (CollUtil.isNotEmpty(toUsers)) {
            postParam.put("touser", toUsers);
        } else if (tagId == null) {
            filter.put("is_to_all", true);
            postParam.put("filter", filter);
        } else {
            filter.put("is_to_all", false);
            filter.put("tag_id", tagId);
            postParam.put("filter", filter);
        }
        postParam.put("msgtype", msgType);
        JSONObject object = new JSONObject();
        if (WxConsts.MassMsgType.MPNEWS.equals(msgType)) {
            postParam.put("send_ignore_reprint", sendIgnoreReprint ? 1 : 0);
            object.put("media_id", mediaId);
            postParam.put("mpnews", object);
        } else if (WxConsts.MassMsgType.IMAGE.equals(msgType)) {
            if (StringUtils.isNotEmptyString(content)) {
                JSONObject jsonObject = JSON.parseObject(content);
                postParam.put("images", jsonObject);
            }
        } else if (WxConsts.MassMsgType.MPVIDEO.equals(msgType)) {
            object.put("media_id", mediaId);
            postParam.put("mpvideo", object);
        } else if (WxConsts.MassMsgType.VOICE.equals(msgType)) {
            object.put("media_id", mediaId);
            postParam.put("voice", object);
        } else if (WxConsts.MassMsgType.TEXT.equals(msgType)) {
            object.put("content", content);
            postParam.put("text", object);
        }
        return postParam;
    }

}
