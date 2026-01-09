package com.caring.sass.common.difyclient.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent模式下思考步骤事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AgentThoughtEvent extends BaseMessageEvent {

    /**
     * agent_thought ID，每一轮Agent迭代都会有一个唯一的id
     */
    @JsonProperty("id")
    private String id;

    /**
     * agent_thought在消息中的位置，如第一轮迭代position为1
     */
    @JsonProperty("position")
    private Integer position;

    /**
     * agent的思考内容
     */
    @JsonProperty("thought")
    private String thought;

    /**
     * 工具调用的返回结果
     */
    @JsonProperty("observation")
    private String observation;

    /**
     * 使用的工具列表，以 ; 分割多个工具
     */
    @JsonProperty("tool")
    private String tool;

    /**
     * 工具的输入，JSON格式的字符串(object)
     */
    @JsonProperty("tool_input")
    private String toolInput;

    /**
     * 当前 agent_thought 关联的文件ID
     */
    @JsonProperty("message_files")
    private List<String> messageFiles;

    /**
     * 文件ID
     */
    @JsonProperty("file_id")
    private String fileId;
}
