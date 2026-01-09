package com.caring.sass.common.difyclient.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 工作流日志响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowLogsResponse {
    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer limit;

    /**
     * 总条数
     */
    private Integer total;

    /**
     * 是否还有更多数据
     */
    private Boolean hasMore;

    /**
     * 当前页码的数据
     */
    private List<WorkflowLogItem> data;

    /**
     * 工作流日志项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowLogItem {
        /**
         * 标识
         */
        private String id;

        /**
         * Workflow 执行日志
         */
        private WorkflowRunInfo workflowRun;

        /**
         * 来源
         */
        private String createdFrom;

        /**
         * 角色
         */
        private String createdByRole;

        /**
         * 帐号
         */
        private String createdByAccount;

        /**
         * 用户
         */
        private EndUser createdByEndUser;

        /**
         * 创建时间
         */
        private Long createdAt;
    }

    /**
     * 工作流执行信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowRunInfo {
        /**
         * 标识
         */
        private String id;

        /**
         * 版本
         */
        private String version;

        /**
         * 执行状态, running / succeeded / failed / stopped
         */
        private String status;

        /**
         * 错误
         */
        private String error;

        /**
         * 耗时，单位秒
         */
        private Double elapsedTime;

        /**
         * 消耗的token数量
         */
        private Integer totalTokens;

        /**
         * 执行步骤长度
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

    /**
     * 终端用户
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndUser {
        /**
         * 标识
         */
        private String id;

        /**
         * 类型
         */
        private String type;

        /**
         * 是否匿名
         */
        private Boolean isAnonymous;

        /**
         * 会话标识
         */
        private String sessionId;

        /**
         * 创建时间
         */
        private Long createdAt;
    }
}
