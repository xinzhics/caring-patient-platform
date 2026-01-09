package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * node 开始执行事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NodeStartedEvent extends BaseWorkflowEvent {

    /**
     * 详细内容
     */
    @JsonProperty("data")
    private NodeStartedData data;

    /**
     * node 开始执行事件数据
     */
    @Data
    @NoArgsConstructor
    public static class NodeStartedData {

        /**
         * workflow 执行 ID
         */
        @JsonProperty("id")
        private String id;

        /**
         * 节点 ID
         */
        @JsonProperty("node_id")
        private String nodeId;

        /**
         * 节点类型
         */
        @JsonProperty("node_type")
        private String nodeType;

        /**
         * 节点名称
         */
        @JsonProperty("title")
        private String title;

        /**
         * 执行序号，用于展示 Tracing Node 顺序
         */
        @JsonProperty("index")
        private Integer index;

        /**
         * 前置节点 ID，用于画布展示执行路径
         */
        @JsonProperty("predecessor_node_id")
        private String predecessorNodeId;

        /**
         * 节点中所有使用到的前置节点变量内容
         */
        @JsonProperty("inputs")
        private Map<String, Object> inputs;

        /**
         * 开始时间
         */
        @JsonProperty("created_at")
        private Long createdAt;
    }
}
