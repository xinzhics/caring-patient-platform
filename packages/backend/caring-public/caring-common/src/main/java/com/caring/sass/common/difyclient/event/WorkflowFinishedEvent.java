package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * workflow 执行结束事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowFinishedEvent extends BaseWorkflowEvent {

    /**
     * 详细内容
     */
    @JsonProperty("data")
    private WorkflowFinishedData data;

    /**
     * workflow 执行结束事件数据
     */
    @Data
    @NoArgsConstructor
    public static class WorkflowFinishedData {

        /**
         * workflow 执行 ID
         */
        @JsonProperty("id")
        private String id;

        /**
         * 关联 Workflow ID
         */
        @JsonProperty("workflow_id")
        private String workflowId;

        /**
         * 执行状态 running / succeeded / failed / stopped
         */
        @JsonProperty("status")
        private String status;

        /**
         * 输出内容
         */
        @JsonProperty("outputs")
        private Map<String, Object> outputs;

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
         * 总使用 tokens
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;

        /**
         * 总步数（冗余），默认 0
         */
        @JsonProperty("total_steps")
        private Integer totalSteps;

        /**
         * 开始时间
         */
        @JsonProperty("created_at")
        private Long createdAt;

        /**
         * 结束时间
         */
        @JsonProperty("finished_at")
        private Long finishedAt;
    }
}
