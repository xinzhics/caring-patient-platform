package com.caring.sass.wx.entity.config;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName WeiXinJsApiTicket
 * @Description
 * @Author yangShuai
 * @Date 2021/12/29 16:34
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_js_api_ticket")
@ApiModel(value = "WeiXinJsApiTicket", description = "第三方平台使用JSSDK需要的jsapi_ticket")
@AllArgsConstructor
public class WXJsApiTicket extends Entity<Long> {


    @TableField(value = "app_id", condition = EQUAL)
    private String appId;

    @TableField("jsapi_ticket")
    private String jsapiTicket;

    @TableField("jsapi_ticket_expired_time")
    private Long jsapiTicketExpiredTime;



}
