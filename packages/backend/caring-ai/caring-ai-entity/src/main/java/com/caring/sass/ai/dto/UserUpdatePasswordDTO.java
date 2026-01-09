package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "UserUpdatePasswordDTO", description = "用户重置密码使用")
public class UserUpdatePasswordDTO {


    @NotNull
    @ApiModelProperty(value = "用户id")
    private Long id;


    @NotNull
    @ApiModelProperty(value = "用户密码")
    @Length(max = 40, message = "用户密码")
    private String password;




}
