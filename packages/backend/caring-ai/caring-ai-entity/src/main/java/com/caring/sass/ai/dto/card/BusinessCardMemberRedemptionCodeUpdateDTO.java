package com.caring.sass.ai.dto.card;

import java.time.LocalDateTime;

import com.caring.sass.ai.entity.card.BusinessCardMemberVersionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
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
@ApiModel(value = "BusinessCardMemberRedemptionCodeUpdateDTO", description = "机构会员兑换码")
public class BusinessCardMemberRedemptionCodeUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 兑换的用户ID
     */
    @ApiModelProperty(value = "兑换的用户ID")
    @NotNull(message = "兑换的用户ID不能为空")
    private Long userId;
    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    private Long organId;
    /**
     * 兑换码
     */
    @ApiModelProperty(value = "兑换码")
    @Length(max = 255, message = "兑换码长度不能超过255")
    private String redemptionCode;
    /**
     * 兑换时间
     */
    @ApiModelProperty(value = "兑换时间")
    private LocalDateTime exchangeTime;
    /**
     * 兑换状态
     */
    @ApiModelProperty(value = "兑换状态")
    @Length(max = 255, message = "兑换状态长度不能超过255")
    private String exchangeStatus;
    /**
     * 兑换码版本
     */
    @ApiModelProperty(value = "兑换码版本")
    private BusinessCardMemberVersionEnum redemptionCodeVersion;
}
