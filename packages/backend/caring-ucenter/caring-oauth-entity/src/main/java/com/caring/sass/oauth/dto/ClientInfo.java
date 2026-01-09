package com.caring.sass.oauth.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xinzh
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "客户端信息")
public class ClientInfo {
    private String clientId;
    private String clientCode;
}
