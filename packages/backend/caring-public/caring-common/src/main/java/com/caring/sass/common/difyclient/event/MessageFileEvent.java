package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文件事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageFileEvent extends BaseEvent {

    /**
     * 文件唯一ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 文件类型，目前仅为image
     */
    @JsonProperty("type")
    private String type;

    /**
     * 文件归属，user或assistant，该接口返回仅为 assistant
     */
    @JsonProperty("belongs_to")
    private String belongsTo;

    /**
     * 文件访问地址
     */
    @JsonProperty("url")
    private String url;

    /**
     * 会话ID
     */
    @JsonProperty("conversation_id")
    private String conversationId;
}
