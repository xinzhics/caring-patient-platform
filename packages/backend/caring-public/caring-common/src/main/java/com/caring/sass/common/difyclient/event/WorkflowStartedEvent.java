package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * workflow 开始执行事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowStartedEvent extends BaseWorkflowEvent {

    /**
     * 详细内容
     */
    @JsonProperty("data")
    private WorkflowStartedData data;

    /**
     * workflow 开始执行事件数据
     */
    @Data
    @NoArgsConstructor
    public static class WorkflowStartedData {

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
         * 自增序号，App 内自增，从 1 开始
         */
        @JsonProperty("sequence_number")
        private Integer sequenceNumber;

        /**
         * 开始时间
         */
        @JsonProperty("created_at")
        private Long createdAt;
    }
}
