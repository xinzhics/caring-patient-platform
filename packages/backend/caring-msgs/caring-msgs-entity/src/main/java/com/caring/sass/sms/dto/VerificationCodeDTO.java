package com.caring.sass.sms.dto;

import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.sms.enumeration.VerificationCodeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 验证码发送验证DTO
 *
 * @author caring
 * @date 2019/08/06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "VerificationCodeDTO", description = "验证码发送验证DTO")
public class VerificationCodeDTO implements Serializable {


    @ApiModelProperty(value = "手机号")
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[^\\s#\\(\\)\\*%,;]+$", message = "mobile手机号参数异常")
    private String mobile;


    @ApiModelProperty(value = "类型")
    @NotNull(message = "类型不能为空")
    private VerificationCodeType type;

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "是否删除key")
    private Boolean clearKey = false;
}
