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
 * @ClassName WeixinUserInfo
 * @Description
 * @Author yangShuai
 * @Date 2022/1/7 16:03
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_user_info")
@ApiModel(value = "WeiXinUserInfo", description = "授权微信信息")
@AllArgsConstructor
public class WeiXinUserInfo extends Entity<Long> {

    @ApiModelProperty("openId")
    @TableField("open_id")
    private String openId;

    @ApiModelProperty("昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty("头像链接")
    @TableField("head_img_url")
    private String headImgUrl;

    @ApiModelProperty("unionId")
    @TableField("union_id")
    private String unionId;


    @ApiModelProperty("用户选择成为的身份")
    @TableField("user_role")
    private String userRole;

}
