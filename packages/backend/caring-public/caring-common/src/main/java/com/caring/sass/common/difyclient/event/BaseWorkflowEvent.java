package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 工作流事件的基类
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BaseWorkflowEvent extends BaseEvent {

    /**
     * 工作流执行ID
     */
    @JsonProperty("workflow_run_id")
    private String workflowRunId;
}
