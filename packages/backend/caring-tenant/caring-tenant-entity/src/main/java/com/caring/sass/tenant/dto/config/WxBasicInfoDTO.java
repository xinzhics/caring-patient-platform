package com.caring.sass.tenant.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
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
@ApiModel(value = "WxBasicInfoDTO", description = "微信基础信息")
public class WxBasicInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "新增不传，修改必传")
    private Long id;

    /**
     * 公众号名字
     */
    @ApiModelProperty(value = "公众号名字")
    @Length(max = 200, message = "公众号名字长度不能超过200")
    private String wxName;

    /**
     * 公众号的原始ID，在微信公众平台中可以找到
     */
    @ApiModelProperty(value = "公众号的原始ID，在微信公众平台中可以找到")
    @Length(max = 200, message = "公众号的原始ID，在微信公众平台中可以找到长度不能超过200")
    private String wxSourceId;

    /**
     * 微信应用ID
     */
    @ApiModelProperty(value = "微信应用ID")
    @Length(max = 200, message = "微信应用ID长度不能超过200")
    private String wxAppId;

    /**
     * 对应微信公众号的App Secret
     */
    @ApiModelProperty(value = "对应微信公众号的App Secret")
    @Length(max = 200, message = "对应微信公众号的App Secret长度不能超过200")
    private String wxAppSecret;

    /**
     * 授权文件名字
     */
    @ApiModelProperty(value = "授权文件内容")
    @Length(max = 200, message = "授权文件内容长度不能超过200")
    private String wxAuthorizationFileName;
}
