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
@ApiModel(value = "CustomGroupingSaveDTO", description = "自定义小组")
public class CustomGroupingPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "小组名字")
    @Length(max = 200, message = "小组名字长度不能超过200字符")
    private String name;

    @ApiModelProperty(value = "角色(doctor, NursingStaff)")
    @Length(max = 100, message = "用户的角色")
    private String roleType;

    @ApiModelProperty(value = "排序 默认0 (从大到小排)")
    private Integer groupSort;

}
