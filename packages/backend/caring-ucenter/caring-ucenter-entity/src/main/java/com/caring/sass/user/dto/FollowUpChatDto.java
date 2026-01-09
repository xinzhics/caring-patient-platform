package com.caring.sass.user.dto;

import lombok.Data;

/**
 * 关注后自动回复
 */
@Data
public class FollowUpChatDto {

    private String tenantCode;

    private Long patientId;

    private String openId;

    private String imAccount;


}
