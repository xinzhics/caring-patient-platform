package com.caring.sass.common.difyclient.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作流执行状态响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRunStatusResponse {
    /**
     * 工作流执行 ID
     */
    private String id;

    /**
     * 关联的工作流 ID
     */
    private String workflowId;

    /**
     * 执行状态 running / succeeded / failed / stopped
     */
    private String status;

    /**
     * 任务输入内容
     */
    private Object inputs;

    /**
     * 任务输出内容
     */
    private Object outputs;

    /**
     * 错误原因
     */
    private String error;

    /**
     * 任务执行总步数
     */
    private Integer totalSteps;

    /**
     * 任务执行总 tokens
     */
    private Integer totalTokens;

    /**
     * 任务开始时间
     */
    private String createdAt;

    /**
     * 任务结束时间
     */
    private String finishedAt;

    /**
     * 耗时(s)
     */
    private Double elapsedTime;
}
