package com.caring.sass.tenant.service.batchBuild;

import lombok.Data;

/**
 * @ClassName BatchBuildMessage
 * @Description 发送给redis的消息实体
 * @Author yangShuai
 * @Date 2021/10/28 17:45
 * @Version 1.0
 */
@Data
public class BatchBuildMessage {

    /**
     * 总任务的ID
     */
    private Long batchBuildTaskId;

    /**
     * 子任务的ID
     */
    private Long batchBuildChildId;

    /**
     * 此时间 表示 总任务开始时的 服务器时间
     * 消息接收后，如果消息中的此时间和数据库的 总任务的最后更新时间不一致，表示任务已经过期
     */
    private Long messageUpdateTime;

    /**
     * 此时间 表示 子任务 服务器时间
     * 消息接收后，如果消息中的此时间和数据库的 子任务的最后更新时间不一致，表示任务已经过期
     */
    private Long childTaskMessageUpdateTime;
}
