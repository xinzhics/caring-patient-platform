package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_card_user_order")
@ApiModel(value = "BusinessCardUserOrder", description = "科普名片会员支付订单")
@AllArgsConstructor
public class BusinessCardUserOrder extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;


    @ApiModelProperty(value = "openId")
    @NotNull(message = "openId不能为空")
    @TableField("open_id")
    private String openId;

    /**
     * 会员版本 BASIC_EDITION， MEMBERSHIP_VERSION
     */
    @ApiModelProperty(value = "会员版本 BASIC_EDITION， MEMBERSHIP_VERSION")
    @TableField(value = "goods_type", condition = EQUAL)
    @Excel(name = "会员版本 BASIC_EDITION， MEMBERSHIP_VERSION")
    private BusinessCardMemberVersionEnum goodsType;

    /**
     * 价格，单位分
     */
    @ApiModelProperty(value = "价格，单位分")
    @TableField("goods_price")
    @Excel(name = "价格，单位分")
    private Integer goodsPrice;

    /**
     * 支付状态  成功 success,   未支付 noPay
     */
    @ApiModelProperty(value = "支付状态  成功 success,   未支付 noPay")
    @TableField(value = "payment_status", condition = EQUAL)
    @Excel(name = "支付状态  成功 success,   未支付 noPay")
    private PaymentStatus paymentStatus;


    @Builder
    public BusinessCardUserOrder(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long userId, BusinessCardMemberVersionEnum goodsType, Integer goodsPrice, PaymentStatus paymentStatus) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userId = userId;
        this.goodsType = goodsType;
        this.goodsPrice = goodsPrice;
        this.paymentStatus = paymentStatus;
    }

}
