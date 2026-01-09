package com.caring.sass.user.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

/**
 * 患者发送 文本消息之后。
 * 使用redis消息队列发送出去的给关键字业务处理的信息
 */
@Data
public class KeywordChatDto {

    private String tenantCode;

    private Long patientId;

    private String content;


    private JSONArray chatSendReads;

}
