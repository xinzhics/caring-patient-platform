package com.caring.sass.ai.dto.know;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订阅人查看自己订阅的博主订单列表
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubscriberBloggerList {


    @ApiModelProperty(value = "博主用户id")
    private Long userId;

    @ApiModelProperty(value = "博主用户名称")
    private String userName;

    @ApiModelProperty(value = "博主用户名称")
    private String userAvatar;

    @ApiModelProperty(value = "用户医院")
    private String userHospital;

    @ApiModelProperty(value = "用户科室")
    private String userDepartment;

    @ApiModelProperty(value = "博主用户职称")
    private String userTitle;

    @ApiModelProperty(value = "会员昵称")
    private String memberNickname;

    /**
     * 商品类型 体验版 专业版
     */
    @ApiModelProperty(value = "商品类型 体验版(月度) 专业版(年度)")
    private KnowMemberType goodsType;

    /**
     * 价格，单位分
     */
    @ApiModelProperty(value = "价格，单位分")
    private Integer goodsPrice;

    @ApiModelProperty(value = "支付时间")
    private LocalDateTime paymentTime;

    @ApiModelProperty(value = "会员到期时间")
    private LocalDateTime membershipExpiration;


}
