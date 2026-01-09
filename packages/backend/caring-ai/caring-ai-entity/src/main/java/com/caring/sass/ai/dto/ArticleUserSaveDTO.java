package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * Ai创作用户
 * </p>
 *
 * @author 杨帅
 * @since 2024-08-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleUserSaveDTO", description = "Ai创作用户")
public class ArticleUserSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    @NonNull
    @NotEmpty
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 20, message = "用户手机号长度不能超过20")
    private String userMobile;

    @ApiModelProperty(value = "手机号验证码")
    @Length(max = 20, message = "手机号验证码")
    private String smsCode;


    @ApiModelProperty(value = "密码")
    @Length(max = 40, message = "密码")
    private String password;



    @ApiModelProperty(value = "当前的用户ID")
    private Long userId;


}
