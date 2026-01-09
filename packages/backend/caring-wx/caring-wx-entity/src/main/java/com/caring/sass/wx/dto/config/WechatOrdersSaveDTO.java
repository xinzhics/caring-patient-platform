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
@ApiModel(value = "WechatOrdersSaveDTO", description = "微信支付订单")
public class WechatOrdersSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公众号ID或小程序ID
     */
    @ApiModelProperty(value = "公众号ID或小程序ID")
    @Length(max = 50, message = "公众号ID或小程序ID长度不能超过100")
    private String appId;
    /**
     * 下单openId
     */
    @ApiModelProperty(value = "下单openId")
    @Length(max = 50, message = "下单openId长度不能超过100")
    private String openId;
    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述")
    @Length(max = 127, message = "商品描述长度不能超过127")
    private String description;
    /**
     * 订单金额 单位是 分
     */
    @ApiModelProperty(value = "订单金额 单位是 分")
    @NotNull
    private Integer amount;
    /**
     * CNY：人民币
     */
    @ApiModelProperty(value = "CNY：人民币")
    @Length(max = 6, message = "CNY：人民币长度不能超过6")
    private String amountCurrency;

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
