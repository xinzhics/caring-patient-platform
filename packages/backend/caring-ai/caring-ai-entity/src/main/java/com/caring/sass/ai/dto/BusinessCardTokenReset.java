package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 知识库用户
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class BusinessCardTokenReset implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID")
    private Long userId;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "token")
    @NotNull
    private String refreshToken;
}
