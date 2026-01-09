package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 机构会员兑换码
 * </p>
 *
 * @author 杨帅
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_card_member_redemption_code")
@ApiModel(value = "BusinessCardMemberRedemptionCode", description = "机构会员兑换码")
@AllArgsConstructor
public class BusinessCardMemberRedemptionCode extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    // 未使用
    public static String EXCHANGE_STATUS_NO_USE = "NO_USE";

    // 已使用
    public static String EXCHANGE_STATUS_USED = "USED";

    /**
     * 兑换的用户ID
     */
    @ApiModelProperty(value = "兑换的用户ID")
    @TableField("user_id")
    @Excel(name = "兑换的用户ID")
    private Long userId;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    @TableField("organ_id")
    @Excel(name = "机构ID")
    private Long organId;

    /**
     * 兑换码
     */
    @ApiModelProperty(value = "兑换码")
    @Length(max = 255, message = "兑换码长度不能超过255")
    @TableField(value = "redemption_code", condition = LIKE)
    @Excel(name = "兑换码")
    private String redemptionCode;

    /**
     * 兑换时间
     */
    @ApiModelProperty(value = "兑换时间")
    @TableField("exchange_time")
    @Excel(name = "兑换时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime exchangeTime;

    /**
     * 兑换状态
     */
    @ApiModelProperty(value = "兑换状态 NO_USE, USED ")
    @Length(max = 255, message = "兑换状态长度不能超过255")
    @TableField(value = "exchange_status", condition = EQUAL)
    @Excel(name = "兑换状态")
    private String exchangeStatus;

    /**
     * 兑换码版本
     */
    @ApiModelProperty(value = "兑换码版本")
    @TableField(value = "redemption_code_version", condition = EQUAL)
    @Excel(name = "兑换码版本")
    private BusinessCardMemberVersionEnum redemptionCodeVersion;

    @ApiModelProperty(value = "是否删除")
    @TableField(value = "delete_flag", condition = EQUAL)
    private Boolean deleteFlag;

    @ApiModelProperty(value = "兑换码用户的名片信息")
    @TableField(exist = false)
    private BusinessCard businessCard;


    @ApiModelProperty(value = "兑换码机构信息")
    @TableField(exist = false)
    private BusinessCardOrgan businessCardOrgan;



}
