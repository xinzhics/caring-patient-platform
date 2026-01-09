package com.caring.sass.wx.dto.config;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 微信配置信息
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ConfigSaveDTO", description = "微信配置信息")
public class ConfigSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "新增不传，修改必传")
    private Long id;

    /**
     * 应用ID
     */
    @ApiModelProperty(value = "应用ID")
    @Length(max = 200, message = "应用ID长度不能超过200")
    private String appId;
    /**
     * 对应微信公众号的App Secret
     */
    @ApiModelProperty(value = "对应微信公众号的App Secret")
    @Length(max = 200, message = "对应微信公众号的App Secret长度不能超过200")
    private String appSecret;
    /**
     * token
     */
    @ApiModelProperty(value = "token")
    @Length(max = 200, message = "token长度不能超过200")
    private String token;
    /**
     * 对应微信的Access Secret
     */
    @ApiModelProperty(value = "对应微信的Access Secret")
    @Length(max = 200, message = "对应微信的Access Secret长度不能超过200")
    private String aseKey;
    /**
     * 公众号的原始ID，在微信公众平台中可以找到
     */
    @ApiModelProperty(value = "公众号的原始ID，在微信公众平台中可以找到")
    @Length(max = 200, message = "公众号的原始ID，在微信公众平台中可以找到长度不能超过200")
    private String sourceId;
    /**
     * 公众号名字
     */
    @ApiModelProperty(value = "公众号名字")
    @Length(max = 200, message = "公众号名字长度不能超过200")
    private String name;
    /**
     * 授权文件名字
     */
    @ApiModelProperty(value = "授权文件内容")
    @Length(max = 200, message = "授权文件内容长度不能超过200")
    private String authorizationFileName;
    /**
     * 微信菜单
     */
    @ApiModelProperty(value = "微信菜单")
    @Length(max = 65535, message = "微信菜单长度不能超过65,535")
    private String menu;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    @NotEmpty(message = "租户编号不能为空")
    @Length(max = 10, message = "租户编号长度不能超过10")
    private String tenantCode;

}
