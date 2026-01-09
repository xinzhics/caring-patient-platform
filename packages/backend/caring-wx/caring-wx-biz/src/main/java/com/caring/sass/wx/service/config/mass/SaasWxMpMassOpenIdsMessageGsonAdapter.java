package com.caring.sass.wx.service.config.mass;

import com.google.gson.*;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

/**
 * @ClassName WxMpMassImageOpenIdsMessageGsonAdapter
 * @Description
 * @Author yangShuai
 * @Date 2021/11/23 9:51
 * @Version 1.0
 */
public class SaasWxMpMassOpenIdsMessageGsonAdapter implements JsonSerializer<WxMpMassOpenIdsMessage> {


    /**
     * {
     *    "touser":[
     *     "OPENID1",
     *     "OPENID2"
     *    ],
     *    "images": {
     *       "media_ids": [
     *          "aaa",
     *          "bbb",
     *          "ccc"
     *       ],
     *       "recommend": "xxx",
     *       "need_open_comment": 1,
     *       "only_fans_can_comment": 0
     *    },
     *    "msgtype":"image"
     * }
     * @param message
     * @param typeOfSrc
     * @param context
     * @return
     */
    @Override
    public JsonElement serialize(WxMpMassOpenIdsMessage message, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject messageJson = new JsonObject();

        JsonArray toUsers = new JsonArray();
        for (String openId : message.getToUsers()) {
            toUsers.add(new JsonPrimitive(openId));
        }
        messageJson.add("touser", toUsers);

        if (WxConsts.MassMsgType.MPNEWS.equals(message.getMsgType())) {
            JsonObject sub = new JsonObject();
            sub.addProperty("media_id", message.getMediaId());
            messageJson.add(WxConsts.MassMsgType.MPNEWS, sub);
        }
        if (WxConsts.MassMsgType.TEXT.equals(message.getMsgType())) {
            JsonObject sub = new JsonObject();
            sub.addProperty("content", message.getContent());
            messageJson.add(WxConsts.MassMsgType.TEXT, sub);
        }
        if (WxConsts.MassMsgType.VOICE.equals(message.getMsgType())) {
            JsonObject sub = new JsonObject();
            sub.addProperty("media_id", message.getMediaId());
            messageJson.add(WxConsts.MassMsgType.VOICE, sub);
        }
        if (WxConsts.MassMsgType.IMAGE.equals(message.getMsgType())) {
            JsonObject sub = new JsonObject();
            sub.addProperty("media_id", message.getMediaId());
            messageJson.add(WxConsts.MassMsgType.IMAGE, sub);
        }
        if (WxConsts.MassMsgType.MPVIDEO.equals(message.getMsgType())) {
            JsonObject sub = new JsonObject();
            sub.addProperty("media_id", message.getMediaId());
            messageJson.add(WxConsts.MassMsgType.MPVIDEO, sub);
        }
        messageJson.addProperty("msgtype", message.getMsgType());

        /*
        开发者可以对群发接口的 send_ignore_reprint 参数进行设置，指定待群发的文章被判定为转载时，是否继续群发。
        当 send_ignore_reprint 参数设置为1时，文章被判定为转载时，且原创文允许转载时，将继续进行群发操作。
        当 send_ignore_reprint 参数设置为0时，文章被判定为转载时，将停止群发操作。
        send_ignore_reprint 默认为0。
         */
        messageJson.addProperty("send_ignore_reprint", message.isSendIgnoreReprint() ? 1 : 0);

        if (StringUtils.isNotEmpty(message.getClientMsgId())) {
            messageJson.addProperty("clientmsgid", message.getClientMsgId());
        }

        return messageJson;
    }
}
