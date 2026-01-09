package com.caring.sass.wx.entity.config;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_config")
@ApiModel(value = "Config", description = "微信配置信息")
@AllArgsConstructor
public class Config extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 应用ID
     */
    @ApiModelProperty(value = "应用ID")
    @Length(max = 200, message = "应用ID长度不能超过200")
    @TableField(value = "app_id", condition = LIKE)
    @Excel(name = "应用ID")
    private String appId;

    /**
     * 对应微信公众号的App Secret
     */
    @ApiModelProperty(value = "对应微信公众号的App Secret")
    @Length(max = 200, message = "对应微信公众号的App Secret长度不能超过200")
    @TableField(value = "app_secret", condition = LIKE)
    @Excel(name = "对应微信公众号的App Secret")
    private String appSecret;

    /**
     * token
     */
    @ApiModelProperty(value = "token")
    @Length(max = 200, message = "token长度不能超过200")
    @TableField(value = "token", condition = LIKE)
    @Excel(name = "token")
    private String token;

    /**
     * 对应微信的Access Secret
     */
    @ApiModelProperty(value = "对应微信的Access Secret")
    @Length(max = 200, message = "对应微信的Access Secret长度不能超过200")
    @TableField(value = "ase_key", condition = LIKE)
    @Excel(name = "对应微信的Access Secret")
    private String aseKey;

    /**
     * 公众号的原始ID，在微信公众平台中可以找到
     */
    @ApiModelProperty(value = "公众号的原始ID，在微信公众平台中可以找到")
    @Length(max = 200, message = "公众号的原始ID，在微信公众平台中可以找到长度不能超过200")
    @TableField(value = "source_id", condition = LIKE)
    @Excel(name = "公众号的原始ID，在微信公众平台中可以找到")
    private String sourceId;

    /**
     * 公众号名字
     */
    @ApiModelProperty(value = "公众号名字")
    @Length(max = 200, message = "公众号名字长度不能超过200")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "公众号名字")
    private String name;

    /**
     * 授权文件名字
     */
    @ApiModelProperty(value = "授权文件内容")
    @Length(max = 200, message = "授权文件内容长度不能超过200")
    @TableField(value = "authorization_file_name", condition = EQUAL)
    @Excel(name = "授权文件内容")
    private String authorizationFileName;

    /**
     * 微信菜单
     */
    @ApiModelProperty(value = "微信菜单")
    @Length(max = 65535, message = "微信菜单长度不能超过65535")
    @TableField("menu")
    @Excel(name = "微信菜单")
    private String menu;

    @ApiModelProperty(value = "第三方授权的公众号，默认为false表示之前手动配置的公众号")
    @TableField("third_authorization")
    private Boolean thirdAuthorization;
    /**
     * 增加微信公众号的权限信息
     * 第三方出示授权二维码后，管理员扫码后，微信回推送公众号授权信息到平台。需要保存到本地
     */
    // 授权的公众号或者小程序Id
    // private String authorizerAppid;  此字段使用 appId;

    @TableField("component_app_id")
    @ApiModelProperty("第三方平台Id")
    private String componentAppId;

    @TableField("third_create_time")
    @ApiModelProperty("授权时间,不可使用此字段判断是否过期")
    private Long thirdCreateTime;

    @ApiModelProperty("换取公众号authorizer_access_token使用的授权码")
    @TableField("authorization_code")
    private String authorizationCode;

    @ApiModelProperty("所属于的租户")
    @TableField("tenant_code")
    private String tenantCode;

    @ApiModelProperty("预授权码")
    @TableField("pre_auth_code")
    private String preAuthCode;

    @ApiModelProperty("授权有效时间")
    @TableField("authorization_code_expired_time")
    private Long authorizationCodeExpiredTime;

    @ApiModelProperty("授权状态 unauthorized / authorized")
    @TableField("auth_status")
    private String authStatus;

    @ApiModelProperty("静默公众号 1 静默 不处理公众号的任何事件和消息")
    @TableField("become_silent")
    private Integer becomeSilent;

    @ApiModelProperty("公众号小程序接口调用令牌")
    @TableField("authorizer_access_token")
    private String authorizerAccessToken;

    // 刷新令牌（在授权的公众号具备API权限时，才有此返回值），
    // 刷新令牌主要用于第三方平台获取和刷新已授权用户的 authorizer_access_token。
    // 一旦丢失，只能让用户重新授权，才能再次拿到新的刷新令牌。用户重新授权后，之前的刷新令牌会失效
    @ApiModelProperty("刷新令牌(非常重要的值，不能丢失)")
    @TableField("authorizer_refresh_token")
    private String authorizerRefreshToken;

    @ApiModelProperty("accessToken的有效时间， 2小时")
    @TableField("expires_in")
    private Long expiresIn;

    @ApiModelProperty("accessToken的获取时间, 本地获取到accessToken时的时间")
    @TableField("access_token_create_time")
    private Long accessTokenCreateTime;

    // https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/Before_Develop/third_party_authority_instructions.html
    @ApiModelProperty("授权给开发者的权限集列表")
    @TableField("func_info_json")
    private String funcInfoJson;

    @ApiModelProperty("要授权的账号类型, 1 表示手机端仅展示公众号；2 表示仅展示小程序，3 表示公众号和小程序都展示")
    @TableField("auth_type")
    private Integer authType;

    @ApiModelProperty("微信二维码")
    @TableField("wx_qr_code_url")
    private String wxQrCodeUrl;


    @Builder
    public Config(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime,
                  String appId, String appSecret, String token, String aseKey,
                  String sourceId, String name, String authorizationFileName, String menu) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.appId = appId;
        this.appSecret = appSecret;
        this.token = token;
        this.aseKey = aseKey;
        this.sourceId = sourceId;
        this.name = name;
        this.authorizationFileName = authorizationFileName;
        this.menu = menu;
    }

}
