package com.caring.sass.ai.dto.card;

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
 * 用户的会员版本
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
@ApiModel(value = "BusinessCardMemberVersionSaveDTO", description = "用户的会员版本")
public class BusinessCardMemberVersionSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    /**
     * 用户版本
     */
    @ApiModelProperty(value = "兑换码")
    @Length(max = 10, message = "兑换码长度不超过10")
    private String redemptionCode;

}
