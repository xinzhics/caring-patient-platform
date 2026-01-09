package com.caring.sass.common.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RedisMessageDto
 * @Description 使用redis做 订阅发布功能 是发送的消息体
 * @Author yangShuai
 * @Date 2022/4/26 14:14
 * @Version 1.0
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisMessageDto {

    /**
     * 消息 的租户
     */
    private String tenantCode;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 患者的im账号
     */
    private String patientImAccount;

}
