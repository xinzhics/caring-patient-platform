package com.caring.sass.wx.entity.config;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName AppComponentToken
 * @Description
 * @Author yangShuai
 * @Date 2021/12/29 16:00
 * @Link: https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/2.0/api/Before_Develop/creat_token.html
 * Token生成说明, 存储微信推送过来的 component_verify_ticket
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_app_component_token")
@ApiModel(value = "AppComponentToken", description = "第三方平台verifyTicket记录表")
@AllArgsConstructor
public class AppComponentToken extends Entity<Long> {

    @TableField(value = "app_id", condition = EQUAL)
    private String appId;

    @TableField(value = "component_app_secret", condition = EQUAL)
    private String componentAppSecret;

    @ApiModelProperty("微信每10分钟推送一次，用于更新component_access_token的凭据")
    @TableField(value = "component_verify_ticket")
    private String componentVerifyTicket;

    /**
     * 默认有效期是两个小时
     */
    @ApiModelProperty("第三方平台申请公众号授权使用的凭据")
    @TableField(value = "component_access_token")
    private String componentAccessToken;

    @ApiModelProperty("accessToken的有效期时间。 申请到时间时当前时间再加6400秒")
    @TableField(value = "component_access_token_expires_in")
    private Long componentAccessTokenExpiresIn;

}
