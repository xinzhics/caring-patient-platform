package com.caring.sass.wx.dto.config;

import java.io.Serializable;

import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;

/**
     * @Author yangShuai
 * @Description 上传素材到 微信公众号后返回结果
 * @Date 2020/9/17 14:57
 *
 * @return
 */
public class WxMpMaterialUploadResult implements Serializable {
    private static final long serialVersionUID = -128818731449449537L;
    private String mediaId;
    private String url;
    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static WxMpMaterialUploadResult fromJson(String json) {
        return (WxMpMaterialUploadResult) WxMpGsonBuilder.create().fromJson(json, WxMpMaterialUploadResult.class);
    }

    public String toString() {
        return "WxMpMaterialUploadResult [media_id=" + this.mediaId + ", url=" + this.url + "]";
    }
}
