package com.caring.sass.oauth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 授权后的token信息
 *
 * @author xinzh
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "客户端认证信息")
public class Oauth2AccessToken implements Serializable {
    private static final long serialVersionUID = 914967629530462926L;
    @ApiModelProperty(value = "令牌")
    private String token;
    @ApiModelProperty(value = "令牌类型")
    private String tokenType;
    @ApiModelProperty(value = "过期时间（秒）")
    private long expire;
    @ApiModelProperty(value = "到期时间")
    private Date expiration;
}
