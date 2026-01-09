package com.caring.sass.common.difyclient.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Workflow 执行响应（阻塞模式）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRunResponse {
    /**
     * Workflow 执行 ID
     */
    private String workflowRunId;

    /**
     * 任务 ID
     */
    private String taskId;

    /**
     * 详细数据
     */
    private WorkflowRunData data;

    /**
     * Workflow 执行数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowRunData {
        /**
         * Workflow 执行 ID
         */
        private String id;

        /**
         * 关联 Workflow ID
         */
        private String workflowId;

        /**
         * 执行状态
         */
        private String status;

        /**
         * 输出内容
         */
        private Map<String, Object> outputs;

        /**
         * 错误原因
         */
        private String error;

        /**
         * 耗时（秒）
         */
        private Double elapsedTime;

        /**
         * 总使用 tokens
         */
        private Integer totalTokens;

        /**
         * 总步数
         */
        private Integer totalSteps;

        /**
         * 开始时间
         */
        private Long createdAt;

        /**
         * 结束时间
         */
        private Long finishedAt;
    }
}
