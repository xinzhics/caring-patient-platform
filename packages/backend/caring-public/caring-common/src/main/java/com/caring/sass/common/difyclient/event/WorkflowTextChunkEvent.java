package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * workflow llm执行过程
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowTextChunkEvent extends BaseWorkflowEvent {

    /**
     * 详细内容
     */
    @JsonProperty("data")
    private WorkflowTextChunkData data;

    /**
     * workflow llm执行过程内容
     */
    @Data
    @NoArgsConstructor
    public static class WorkflowTextChunkData {

        /**
         * workflow llm执行文本
         */
        @JsonProperty("text")
        private String text;

        /**
         * workflow llm执行文本来源变量选择器
         */
        @JsonProperty("from_variable_selector")
        private List<String> fromVariableSelector;
    }
}
