package com.caring.sass.common.difyclient.event;

import com.caring.sass.common.difyclient.enums.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 所有事件的基类
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEvent {

    /**
     * 事件类型
     */
    @JsonProperty("event")
    private String event;

    /**
     * 创建时间戳
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * 任务ID，用于请求跟踪和停止响应
     */
    @JsonProperty("task_id")
    private String taskId;

    /**
     * 获取事件类型
     *
     * @return 事件类型枚举
     */
    public EventType getEventType() {
        return EventType.fromValue(event);
    }
}
