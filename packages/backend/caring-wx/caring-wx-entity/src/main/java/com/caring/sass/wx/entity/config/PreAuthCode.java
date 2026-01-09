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

/**
 * @ClassName PreAuthCode
 * @Description 项目发起授权时申请的 预授权码
 * @Author yangShuai
 * @Date 2021/12/31 9:44
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_pre_auth_code")
@ApiModel(value = "PreAuthCode", description = "平台申请的预授权码用于授权公众号或小程序")
@AllArgsConstructor
public class PreAuthCode extends Entity<Long> {


    @ApiModelProperty("预授权码")
    @TableField("pre_auth_code")
    private String preAuthCode;


    @ApiModelProperty("租户")
    @TableField("tenant_code")
    private String tenantCode;


    @ApiModelProperty("1 表示手机端仅展示公众号；2 表示仅展示小程序，3 表示公众号和小程序都展示。\n" +
            "- 4表示小程序推客账号；\n" +
            "- 5表示视频号账号；\n" +
            "- 6表示全部，即公众号、小程序、视频号都展示")
    @TableField("auth_type")
    private Integer authType;

}
