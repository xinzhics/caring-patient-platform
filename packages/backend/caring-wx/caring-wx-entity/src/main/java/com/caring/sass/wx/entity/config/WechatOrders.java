package com.caring.sass.wx.entity.config;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.wx.dto.config.PrepayWithRequestPaymentResponseDTO;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 微信支付订单
 * </p>
 *
 * @author 杨帅
 * @since 2024-06-20
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_wechat_orders")
@ApiModel(value = "WechatOrders", description = "微信支付订单")
@AllArgsConstructor
public class WechatOrders extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 公众号ID或小程序ID
     */
    @ApiModelProperty(value = "公众号ID或小程序ID")
    @Length(max = 100, message = "公众号ID或小程序ID长度不能超过100")
    @TableField(value = "app_id", condition = LIKE)
    @Excel(name = "公众号ID或小程序ID")
    private String appId;

    /**
     * 商户号
     */
    @ApiModelProperty(value = "商户号")
    @Length(max = 100, message = "商户号长度不能超过100")
    @TableField(value = "mchid", condition = LIKE)
    @Excel(name = "商户号")
    private String mchid;

    /**
     * 下单openId
     */
    @ApiModelProperty(value = "下单openId")
    @Length(max = 100, message = "下单openId长度不能超过100")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "下单openId")
    private String openId;

    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述")
    @Length(max = 127, message = "商品描述长度不能超过127")
    @TableField(value = "description", condition = LIKE)
    @Excel(name = "商品描述")
    private String description;

    /**
     * 交易结束时间
     */
    @ApiModelProperty(value = "交易结束时间")
    @TableField("time_expire")
    @Excel(name = "交易结束时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime timeExpire;

    /**
     * 订单金额 单位是 分
     */
    @ApiModelProperty(value = "订单金额 单位是 分")
    @TableField("amount")
    @Excel(name = "订单金额 单位是 分")
    private Integer amount;

    /**
     * CNY：人民币
     */
    @ApiModelProperty(value = "CNY：人民币")
    @Length(max = 6, message = "CNY：人民币长度不能超过6")
    @TableField(value = "amount_currency", condition = LIKE)
    @Excel(name = "CNY：人民币")
    private String amountCurrency;

    /**
     * 预支付交易会话标识
     */
    @ApiModelProperty(value = "预支付交易会话标识")
    @Length(max = 100, message = "预支付交易会话标识长度不能超过100")
    @TableField(value = "prepay_id", condition = LIKE)
    @Excel(name = "预支付交易会话标识")
    private String prepayId;

    /**
     * 错误码
     */
    @ApiModelProperty(value = "错误码")
    @Length(max = 100, message = "错误码长度不能超过100")
    @TableField(value = "error_code", condition = LIKE)
    @Excel(name = "错误码")
    private String errorCode;

    /**
     * 错误描述
     */
    @ApiModelProperty(value = "错误描述")
    @Length(max = 100, message = "错误描述长度不能超过100")
    @TableField(value = "error_desc", condition = LIKE)
    @Excel(name = "错误描述")
    private String errorDesc;

    /**
     * 支付通知ID
     */
    @ApiModelProperty(value = "支付通知ID")
    @Length(max = 100, message = "支付通知ID长度不能超过100")
    @TableField(value = "notify_id", condition = LIKE)
    @Deprecated
    @Excel(name = "支付通知ID")
    private String notifyId;

    /**
     * 通知创建时间
     */
    @ApiModelProperty(value = "通知创建时间")
    @TableField("notify_create_time")
    @Excel(name = "通知创建时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime notifyCreateTime;

    /**
     * 通知类型 支付成功通知的类型为TRANSACTION.SUCCESS
     */
    @ApiModelProperty(value = "通知类型 支付成功通知的类型为TRANSACTION.SUCCESS")
    @Length(max = 100, message = "通知类型 支付成功通知的类型为TRANSACTION.SUCCESS长度不能超过100")
    @TableField(value = "notify_event_type", condition = LIKE)
    @Excel(name = "通知类型 支付成功通知的类型为TRANSACTION.SUCCESS")
    private String notifyEventType;

    /**
     * 通知的资源数据类型 支付成功通知为encrypt-resource
     */
    @ApiModelProperty(value = "通知的资源数据类型 支付成功通知为encrypt-resource")
    @Length(max = 100, message = "通知的资源数据类型 支付成功通知为encrypt-resource长度不能超过100")
    @TableField(value = "notify_resource_type", condition = LIKE)
    @Excel(name = "通知的资源数据类型 支付成功通知为encrypt-resource")
    private String notifyResourceType;

    /**
     * 回调摘要
     */
    @ApiModelProperty(value = "回调摘要")
    @Length(max = 100, message = "回调摘要长度不能超过100")
    @TableField(value = "notify_summary", condition = LIKE)
    @Excel(name = "回调摘要")
    private String notifySummary;

    /**
     * 微信支付订单号
     */
    @ApiModelProperty(value = "微信支付订单号")
    @Length(max = 100, message = "微信支付订单号长度不能超过100")
    @TableField(value = "transaction_id", condition = LIKE)
    @Excel(name = "微信支付订单号")
    private String transactionId;

    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型")
    @Length(max = 100, message = "交易类型长度不能超过100")
    @TableField(value = "trade_type", condition = LIKE)
    @Excel(name = "交易类型")
    private String tradeType;

    /**
     * 付款银行
     */
    @ApiModelProperty(value = "付款银行")
    @Length(max = 100, message = "付款银行长度不能超过100")
    @TableField(value = "bank_type", condition = LIKE)
    @Excel(name = "付款银行")
    private String bankType;

    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态")
    @Length(max = 100, message = "交易状态长度不能超过100")
    @TableField(value = "trade_state", condition = LIKE)
    @Excel(name = "交易状态")
    private String tradeState;

    /**
     * 支付完成时间
     */
    @ApiModelProperty(value = "支付完成时间")
    @Length(max = 100, message = "支付完成时间长度不能超过100")
    @TableField(value = "success_time", condition = LIKE)
    @Excel(name = "支付完成时间")
    private String successTime;

    /**
     * 用户支付金额
     */
    @ApiModelProperty(value = "用户支付金额")
    @TableField("payer_total")
    @Excel(name = "用户支付金额")
    private Integer payerTotal;

    /**
     * 用户支付币种
     */
    @ApiModelProperty(value = "用户支付币种")
    @TableField("payer_currency")
    @Excel(name = "用户支付币种")
    private String payerCurrency;

    /**
     * 支付者
     */
    @ApiModelProperty(value = "支付者")
    @Length(max = 100, message = "支付者长度不能超过100")
    @TableField(value = "payer_openid", condition = LIKE)
    @Excel(name = "支付者")
    private String payerOpenid;

    /**
     * 业务的ID
     */
    @ApiModelProperty(value = "业务的ID")
    @TableField("business_id")
    @Excel(name = "业务的ID")
    private Long businessId;

    /**
     * 业务类
     */
    @ApiModelProperty(value = "业务类")
    @Length(max = 255, message = "业务类长度不能超过255")
    @TableField(value = "business_type", condition = LIKE)
    @Excel(name = "业务类")
    private String businessType;


    @ApiModelProperty(value = "支付方式 小程序 H5 native")
    @Length(max = 255, message = "业务类长度不能超过255")
    @TableField(value = "pay_way", condition = EQUAL)
    private String payWay;


    @ApiModelProperty(value = "业务回调接口路径")
    @TableField(value = "notify_url", condition = LIKE)
    private String notifyUrl;

    @ApiModelProperty(value = "退款单号")
    @TableField(value = "refund_id", condition = EQUAL)
    private String refundId;

    @ApiModelProperty(value = "退款渠道")
    @TableField(value = "refund_channel", condition = EQUAL)
    private String refundChannel;

    @ApiModelProperty(value = "取当前退款单的退款入账方")
    @TableField(value = "user_received_account", condition = EQUAL)
    private String userReceivedAccount;

    @ApiModelProperty(value = "退款成功时间")
    @TableField(value = "refund_success_time", condition = EQUAL)
    private String refundSuccessTime;

    @ApiModelProperty(value = "退款状态")
    @TableField(value = "refund_status", condition = EQUAL)
    private String refundStatus;

    @ApiModelProperty(value = "退款所使用资金对应的资金账户类型")
    @TableField(value = "refund_account", condition = EQUAL)
    private String refundsAccount;

    @ApiModelProperty(value = "退款金额")
    @TableField(value = "refund_amount", condition = EQUAL)
    private String refundAmount;

    @ApiModelProperty(value = "小程序调起支付时的签名信息")
    @TableField(exist = false)
    private PrepayWithRequestPaymentResponseDTO paymentResponse;


}
