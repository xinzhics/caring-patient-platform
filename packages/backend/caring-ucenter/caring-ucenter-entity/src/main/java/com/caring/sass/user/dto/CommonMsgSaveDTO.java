package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 小组
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CommonMsgSaveDTO", description = "常用语")
public class CommonMsgSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "专员Id")
    private Long accountId;
    /**
     * 常用语
     */
    @ApiModelProperty(value = "常用语")
    @Length(max = 800, message = "常用语长度不能超过800字")
    private String content;

    @ApiModelProperty(value = "用户类型 NursingStaff, doctor")
    private String userType;

}
