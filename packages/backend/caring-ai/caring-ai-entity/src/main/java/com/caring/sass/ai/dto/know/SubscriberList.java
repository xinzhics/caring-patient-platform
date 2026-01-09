package com.caring.sass.ai.dto.know;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 博主查看订阅者列表
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubscriberList {


    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户手机")
    private String userMobile;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;


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
