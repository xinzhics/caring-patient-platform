package com.caring.sass.common.difyclient.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对话会话列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationListResponse {
    /**
     * 返回条数
     */
    private Integer limit;

    /**
     * 是否存在下一页
     */
    private Boolean hasMore;

    /**
     * 会话列表
     */
    private List<Conversation> data;
}
