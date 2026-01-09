package com.caring.sass.ai.entity.know;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.ai.dto.know.KnowMemberType;
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

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 知识库用户购买会员订单
 * </p>
 *
 * @author 杨帅
 * @since 2024-10-11
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_user_order")
@ApiModel(value = "KnowledgeUserOrder", description = "知识库用户购买会员订单")
@AllArgsConstructor
public class KnowledgeUserOrder extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    @ApiModelProperty(value = "博主域名")
    @NotNull(message = "用户id不能为空")
    @TableField("user_domain")
    private String userDomain;

    /**
     * seeUid
     */
    @ApiModelProperty(value = "seeUid")
    @Length(max = 50, message = "seeUid长度不能超过50")
    @TableField(value = "see_uid", condition = LIKE)
    @Excel(name = "seeUid")
    private String seeUid;

    /**
     * 商品类型 体验版 专业版
     */
    @ApiModelProperty(value = "商品类型 体验版(月度) 专业版(年度)")
    @TableField(value = "goods_type", condition = EQUAL)
    private KnowMemberType goodsType;

    /**
     * 价格，单位分
     */
    @ApiModelProperty(value = "价格，单位分")
    @TableField("goods_price")
    @Excel(name = "价格，单位分")
    private Integer goodsPrice;

    /**
     * 支付状态
     */
    @ApiModelProperty(value = "支付状态")
    @Length(max = 255, message = "支付状态长度不能超过255")
    @TableField(value = "payment_status", condition = LIKE)
    @Excel(name = "支付状态")
    private PaymentStatus paymentStatus;

    @ApiModelProperty(value = "支付时间")
    @TableField(value = "payment_time")
    private LocalDateTime paymentTime;

    @ApiModelProperty(value = "会员到期时间")
    @TableField(value = "membership_expiration")
    private LocalDateTime membershipExpiration;


    @ApiModelProperty(value = "剩余会员抵扣金额")
    @TableField("deductible_amount")
    private Integer deductibleAmount;

}
