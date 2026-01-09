package com.caring.sass.ai.entity.call;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 通话充值订单
 * </p>
 *
 * @author 杨帅
 * @since 2025-12-02
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_call_recharge_order")
@ApiModel(value = "CallRechargeOrder", description = "通话充值订单")
@AllArgsConstructor
public class CallRechargeOrder extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * seeUid
     */
    @ApiModelProperty(value = "seeUid")
    @Length(max = 50, message = "seeUid长度不能超过50")
    @TableField(value = "see_uid", condition = LIKE)
    @Excel(name = "seeUid")
    private String seeUid;

    @ApiModelProperty(value = "分钟时长")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "goods_type", condition = LIKE)
    private String goodsType;

    /**
     * 分钟时长
     */
    @ApiModelProperty(value = "分钟时长")
    @TableField("minute_duration")
    @Excel(name = "分钟时长")
    private Integer minuteDuration;

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

    /**
     * 订单支付时间
     */
    @ApiModelProperty(value = "订单支付时间")
    @TableField("payment_time")
    @Excel(name = "订单支付时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime paymentTime;

     /**
     * 微信支付链接
     */
    @ApiModelProperty(value = "微信支付链接")
    @Length(max = 255, message = "微信支付链接长度不能超过255")
    @TableField(value = "wx_pay_url", exist = false)
    private String wxPayUrl;


    @Builder
    public CallRechargeOrder(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long userId, String seeUid, String goodsType, Integer goodsPrice, PaymentStatus paymentStatus, LocalDateTime paymentTime) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userId = userId;
        this.seeUid = seeUid;
        this.goodsType = goodsType;
        this.goodsPrice = goodsPrice;
        this.paymentStatus = paymentStatus;
        this.paymentTime = paymentTime;
    }

}
