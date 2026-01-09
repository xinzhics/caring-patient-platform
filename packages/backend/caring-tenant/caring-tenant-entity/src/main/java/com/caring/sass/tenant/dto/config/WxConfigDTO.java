package com.caring.sass.tenant.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author leizhi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "WxConfigDTO", description = "微信配置信息")
public class WxConfigDTO implements Serializable {

    private static final long serialVersionUID = 7161794401615772073L;

    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private String tenantCode;

    /**
     * ip白名单
     */
    @ApiModelProperty(value = "ip白名单")
    private String whitelist;

    /**
     * 服务器配置url
     */
    @ApiModelProperty(value = "服务器配置url")
    private String serverUrl;

    /**
     * 服务器配置token
     */
    @ApiModelProperty(value = "服务器配置token")
    private String serverToken;

    /**
     * 业务域名
     */
    @ApiModelProperty(value = "业务域名")
    private String bizDomain;
}
