package com.caring.sass.ai.dto.card;

import com.caring.sass.ai.entity.card.BusinessCardMemberVersionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
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
@ApiModel(value = "BusinessCardMemberRedemptionCodeSaveDTO", description = "机构会员兑换码")
public class BusinessCardMemberRedemptionCodeSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 机构ID
     */
    @NotNull
    @ApiModelProperty(value = "机构ID")
    private Long organId;
    /**
     * 兑换码
     */
    @NotNull
    @ApiModelProperty(value = "创建兑换码数量")
    private Integer number;
    /**
     * 兑换码版本
     */
    @ApiModelProperty(value = "兑换码版本")
    private BusinessCardMemberVersionEnum redemptionCodeVersion = BusinessCardMemberVersionEnum.MEMBERSHIP_VERSION;

}
