package com.caring.sass.ai.entity.textual;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.article.ArticleOrderGoodsType;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * 文献解读用户支付订单
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_textual_interpretation_user_order")
@ApiModel(value = "TextualInterpretationUserOrder", description = "文献解读用户支付订单")
@AllArgsConstructor
public class TextualInterpretationUserOrder extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 会员充值，能量币充值
     */
    @ApiModelProperty(value = "会员充值，能量币充值")
    @Length(max = 255, message = "会员充值，能量币充值长度不能超过255")
    @TableField(value = "goods_type", condition = LIKE)
    @Excel(name = "会员充值，能量币充值")
    private ArticleOrderGoodsType goodsType;

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
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 40, message = "openId长度不能超过40")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "openId")
    private String openId;

    /**
     * 退款时间
     */
    @ApiModelProperty(value = "退款时间")
    @TableField("refund_time")
    @Excel(name = "退款时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime refundTime;

    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    @TableField("pay_time")
    @Excel(name = "支付时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime payTime;

    /**
     * uid
     */
    @ApiModelProperty(value = "uid")
    @Length(max = 255, message = "uid长度不能超过255")
    @TableField(value = "uid", condition = LIKE)
    @Excel(name = "uid")
    private String uid;



}
