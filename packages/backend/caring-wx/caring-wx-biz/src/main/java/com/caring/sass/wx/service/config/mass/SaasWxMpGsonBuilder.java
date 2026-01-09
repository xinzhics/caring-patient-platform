package com.caring.sass.wx.service.config.mass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.WxMpMassPreviewMessage;

/**
 * @author someone
 */
public class SaasWxMpGsonBuilder {

  private static final GsonBuilder INSTANCE = new GsonBuilder();

  static {
    INSTANCE.disableHtmlEscaping();
    INSTANCE.registerTypeAdapter(WxMpMassPreviewMessage.class, new SaasWxMpMassPreviewMessageGsonAdapter());
    INSTANCE.registerTypeAdapter(WxMpMassOpenIdsMessage.class, new SaasWxMpMassOpenIdsMessageGsonAdapter());
    INSTANCE.registerTypeAdapter(WxMpMassOpenIdsMessage.class, new SaasWxMpMassOpenIdsMessageGsonAdapter());
  }

  public static Gson create() {
    return INSTANCE.create();
  }

}
