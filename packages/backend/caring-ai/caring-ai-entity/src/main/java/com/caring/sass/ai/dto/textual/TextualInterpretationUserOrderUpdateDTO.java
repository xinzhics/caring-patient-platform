package com.caring.sass.ai.dto.textual;

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
 * 文献解读用户支付订单
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TextualInterpretationUserOrderUpdateDTO", description = "文献解读用户支付订单")
public class TextualInterpretationUserOrderUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 会员充值，能量币充值
     */
    @ApiModelProperty(value = "会员充值，能量币充值")
    @Length(max = 255, message = "会员充值，能量币充值长度不能超过255")
    private String goodsType;
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
    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 40, message = "openId长度不能超过40")
    private String openId;
    /**
     * 退款时间
     */
    @ApiModelProperty(value = "退款时间")
    private LocalDateTime refundTime;
    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private LocalDateTime payTime;
    /**
     * uid
     */
    @ApiModelProperty(value = "uid")
    @Length(max = 255, message = "uid长度不能超过255")
    private String uid;
}
