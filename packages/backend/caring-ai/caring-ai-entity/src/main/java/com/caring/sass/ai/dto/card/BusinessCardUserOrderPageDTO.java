package com.caring.sass.ai.dto.card;

import com.caring.sass.ai.entity.card.BusinessCardMemberVersionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 科普名片会员支付订单
 * </p>
 *
 * @author 杨帅
 * @since 2025-01-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BusinessCardUserOrderPageDTO", description = "科普名片会员支付订单")
public class BusinessCardUserOrderPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;
    /**
     * 会员版本 BASIC_EDITION， MEMBERSHIP_VERSION
     */
    @ApiModelProperty(value = "会员版本 BASIC_EDITION， MEMBERSHIP_VERSION")
    private BusinessCardMemberVersionEnum goodsType;
    /**
     * 价格，单位分
     */
    @ApiModelProperty(value = "价格，单位分")
    private Integer goodsPrice;
    /**
     * 支付状态  成功 success,   未支付 noPay
     */
    @ApiModelProperty(value = "支付状态  成功 success,   未支付 noPay")
    @Length(max = 255, message = "支付状态  成功 success,   未支付 noPay长度不能超过255")
    private String paymentStatus;

}
