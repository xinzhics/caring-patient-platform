package com.caring.sass.ai.dto.card;

import java.time.LocalDateTime;

import com.caring.sass.ai.entity.card.BusinessCardMemberVersionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BusinessCardMemberRedemptionCodePageDTO", description = "机构会员兑换码")
public class BusinessCardMemberRedemptionCodePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 兑换的用户ID
     */
    @ApiModelProperty(value = "兑换的用户ID")
    private Long userId;


    @ApiModelProperty(value = "医生名称")
    @Length(max = 20, message = "医生名称长度不能超过20")
    private String doctorName;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    private Long organId;
    /**
     * 兑换码
     */
    @ApiModelProperty(value = "兑换码")
    @Length(max = 10, message = "兑换码长度不能超过10")
    private String redemptionCode;
    /**
     * 兑换时间
     */
    @ApiModelProperty(value = "兑换时间")
    private LocalDateTime exchangeStartTime;

    @ApiModelProperty(value = "兑换结束时间")
    private LocalDateTime exchangeEndTime;
    /**
     * 兑换状态
     */
    @ApiModelProperty(value = "兑换状态 USED  NO_USE")
    @Length(max = 255, message = "兑换状态长度不能超过255")
    private String exchangeStatus;
    /**
     * 兑换码版本
     */
    @ApiModelProperty(value = "兑换码版本")
    private BusinessCardMemberVersionEnum redemptionCodeVersion;

}
