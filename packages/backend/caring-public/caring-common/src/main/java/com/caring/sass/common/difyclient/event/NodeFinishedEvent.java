package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * node 执行结束事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NodeFinishedEvent extends BaseWorkflowEvent {

    /**
     * 详细内容
     */
    @JsonProperty("data")
    private NodeFinishedData data;

    /**
     * node 执行结束事件数据
     */
    @Data
    @NoArgsConstructor
    public static class NodeFinishedData {

        /**
         * node 执行 ID
         */
        @JsonProperty("id")
        private String id;

        /**
         * 节点 ID
         */
        @JsonProperty("node_id")
        private String nodeId;

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
         * 节点过程数据
         */
        @JsonProperty("process_data")
        private Map<String, Object> processData;

        /**
         * 输出内容
         */
        @JsonProperty("outputs")
        private Map<String, Object> outputs;

        /**
         * 执行状态 running / succeeded / failed / stopped
         */
        @JsonProperty("status")
        private String status;

        /**
         * 错误原因
         */
        @JsonProperty("error")
        private String error;

        /**
         * 耗时(s)
         */
        @JsonProperty("elapsed_time")
        private Double elapsedTime;

        /**
         * 元数据
         */
        @JsonProperty("execution_metadata")
        private Map<String, Object> executionMetadata;

        /**
         * 总使用 tokens
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;

        /**
         * 总费用
         */
        @JsonProperty("total_price")
        private Double totalPrice;

        /**
         * 货币，如 USD / RMB
         */
        @JsonProperty("currency")
        private String currency;

        /**
         * 开始时间
         */
        @JsonProperty("created_at")
        private Long createdAt;
    }
}
