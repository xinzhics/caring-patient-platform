package com.caring.sass.oauth.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 获取token的请求参数
 *
 * @author xinzh
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "获取token的请求参数")
public class TokenRequest implements Serializable {

    private static final long serialVersionUID = 862878915605741178L;

    /**
     * 客户端id
     */
    @NotEmpty(message = "客户端id不能为空")
    private String clientId;

    /**
     * 客户端秘钥
     */
    @NotEmpty(message = "客户端秘钥不能为空")
    private String clientSecret;

    /**
     * token过期时间
     */
    private Long expireMillis;
}
