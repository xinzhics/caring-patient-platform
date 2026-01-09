package com.caring.sass.ai.entity.ckd;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.ai.dto.ckd.CkdUserGoodsType;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * ckd会员订单
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-08
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_ckd_user_order")
@ApiModel(value = "CkdUserOrder", description = "ckd会员订单")
@AllArgsConstructor
public class CkdUserOrder extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 会员版本 9.9， 99， 199
     */
    @ApiModelProperty(value = "会员版本 9.9， 99， 199")
    @Length(max = 255, message = "会员版本 9.9， 99， 199长度不能超过255")
    @TableField(value = "goods_type", condition = LIKE)
    @Excel(name = "会员版本 free, 9.9， 99， 199")
    private CkdUserGoodsType goodsType;

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
    @Length(max = 255, message = "支付状态  成功 success,   未支付 noPay长度不能超过255")
    @TableField(value = "payment_status", condition = LIKE)
    @Excel(name = "支付状态  成功 success,   未支付 noPay")
    private PaymentStatus paymentStatus;


    @ApiModelProperty(value = "支付时间")
    @TableField(value = "pay_time")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "退款时间")
    @TableField(value = "refund_time")
    private LocalDateTime refundTime;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 40, message = "openId长度不能超过40")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "openId")
    private String openId;




}
