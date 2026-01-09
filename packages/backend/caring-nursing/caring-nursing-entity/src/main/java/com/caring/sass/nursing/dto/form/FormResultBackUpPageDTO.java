package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @ClassName FromResultBackUpPageDTO
 * @Description
 * @Author yangShuai
 * @Date 2022/4/12 17:33
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FromResultBackUpPageDTO", description = "表单填写修改记录表")
public class FormResultBackUpPageDTO {

    /**
     * 表单Id
     */
    @ApiModelProperty(value = "表单结果Id")
    @NotNull(message = "表单结果Id不能为空")
    private Long formResultId;

    /**
     * 填写人Id
     */
    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    /**
     * 用户角色
     */
    @ApiModelProperty(value = "修改人角色")
    private String userType;
}
