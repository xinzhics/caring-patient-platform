package com.caring.sass.ai.dto.humanVideo;

import com.alibaba.fastjson.JSON;

/**
 * 存放需要处理百度视频的参数
 */
public class BaiduVideoDTO {


    private String businessId;

    private String businessClassName;


    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessClassName() {
        return businessClassName;
    }

    public void setBusinessClassName(String businessClassName) {
        this.businessClassName = businessClassName;
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    public static BaiduVideoDTO fromJSONString(String json) {
        return JSON.parseObject(json, BaiduVideoDTO.class);
    }
}
