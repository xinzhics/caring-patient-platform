package com.caring.sass.wx.dto.config;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "WechatOrdersUpdateDTO", description = "微信支付订单")
public class WechatOrdersUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 公众号ID或小程序ID
     */
    @ApiModelProperty(value = "公众号ID或小程序ID")
    @Length(max = 100, message = "公众号ID或小程序ID长度不能超过100")
    private String appId;
    /**
     * 商户号
     */
    @ApiModelProperty(value = "商户号")
    @Length(max = 100, message = "商户号长度不能超过100")
    private String mchid;
    /**
     * 下单openId
     */
    @ApiModelProperty(value = "下单openId")
    @Length(max = 100, message = "下单openId长度不能超过100")
    private String openId;
    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述")
    @Length(max = 127, message = "商品描述长度不能超过127")
    private String description;
    /**
     * 交易结束时间
     */
    @ApiModelProperty(value = "交易结束时间")
    private LocalDateTime timeExpire;
    /**
     * 订单金额 单位是 分
     */
    @ApiModelProperty(value = "订单金额 单位是 分")
    private Integer amount;
    /**
     * CNY：人民币
     */
    @ApiModelProperty(value = "CNY：人民币")
    @Length(max = 6, message = "CNY：人民币长度不能超过6")
    private String amountCurrency;
    /**
     * 预支付交易会话标识
     */
    @ApiModelProperty(value = "预支付交易会话标识")
    @Length(max = 100, message = "预支付交易会话标识长度不能超过100")
    private String prepayId;
    /**
     * 错误码
     */
    @ApiModelProperty(value = "错误码")
    @Length(max = 100, message = "错误码长度不能超过100")
    private String errorCode;
    /**
     * 错误描述
     */
    @ApiModelProperty(value = "错误描述")
    @Length(max = 100, message = "错误描述长度不能超过100")
    private String errorDesc;
    /**
     * 支付通知ID
     */
    @ApiModelProperty(value = "支付通知ID")
    @Length(max = 100, message = "支付通知ID长度不能超过100")
    private String notifyId;
    /**
     * 通知创建时间
     */
    @ApiModelProperty(value = "通知创建时间")
    private LocalDateTime notifyCreateTime;
    /**
     * 通知类型 支付成功通知的类型为TRANSACTION.SUCCESS
     */
    @ApiModelProperty(value = "通知类型 支付成功通知的类型为TRANSACTION.SUCCESS")
    @Length(max = 100, message = "通知类型 支付成功通知的类型为TRANSACTION.SUCCESS长度不能超过100")
    private String notifyEventType;
    /**
     * 通知的资源数据类型 支付成功通知为encrypt-resource
     */
    @ApiModelProperty(value = "通知的资源数据类型 支付成功通知为encrypt-resource")
    @Length(max = 100, message = "通知的资源数据类型 支付成功通知为encrypt-resource长度不能超过100")
    private String notifyResourceType;
    /**
     * 回调摘要
     */
    @ApiModelProperty(value = "回调摘要")
    @Length(max = 100, message = "回调摘要长度不能超过100")
    private String notifySummary;
    /**
     * 微信支付订单号
     */
    @ApiModelProperty(value = "微信支付订单号")
    @Length(max = 100, message = "微信支付订单号长度不能超过100")
    private String transactionId;
    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型")
    @Length(max = 100, message = "交易类型长度不能超过100")
    private String tradeType;
    /**
     * 付款银行
     */
    @ApiModelProperty(value = "付款银行")
    @Length(max = 100, message = "付款银行长度不能超过100")
    private String bankType;
    /**
     * 交易状态
     */
    @ApiModelProperty(value = "交易状态")
    @Length(max = 100, message = "交易状态长度不能超过100")
    private String tradeState;
    /**
     * 支付完成时间
     */
    @ApiModelProperty(value = "支付完成时间")
    @Length(max = 100, message = "支付完成时间长度不能超过100")
    private String successTime;
    /**
     * 用户支付金额
     */
    @ApiModelProperty(value = "用户支付金额")
    private Integer payerTotal;
    /**
     * 用户支付币种
     */
    @ApiModelProperty(value = "用户支付币种")
    private Integer payerCurrency;
    /**
     * 支付者
     */
    @ApiModelProperty(value = "支付者")
    @Length(max = 100, message = "支付者长度不能超过100")
    private String payerOpenid;
    /**
     * 业务的ID
     */
    @ApiModelProperty(value = "业务的ID")
    private Long businessId;
    /**
     * 业务类
     */
    @ApiModelProperty(value = "业务类")
    @Length(max = 255, message = "业务类长度不能超过255")
    private String businessType;
}
