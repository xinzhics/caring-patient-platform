package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("用户订阅模版一次性")
public class UserSubscriptionTemplate {

    Long userId;

    String templateId;

}
