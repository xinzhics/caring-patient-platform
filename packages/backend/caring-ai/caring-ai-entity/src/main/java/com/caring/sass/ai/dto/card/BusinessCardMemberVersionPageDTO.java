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
@ApiModel(value = "BusinessCardMemberVersionPageDTO", description = "用户的会员版本")
public class BusinessCardMemberVersionPageDTO implements Serializable {

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
    @ApiModelProperty(value = "用户版本")
    @Length(max = 255, message = "用户版本长度不能超过255")
    private BusinessCardMemberVersionEnum memberVersion;
    /**
     * 版本到期时间
     */
    @ApiModelProperty(value = "版本到期时间")
    private LocalDateTime expirationDate;

}
