package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

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
@ApiModel(value = "CustomGroupingUpdateDTO", description = "自定义小组")
public class CustomGroupingUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long id;

    @ApiModelProperty(value = "小组名字")
    @Length(max = 200, message = "小组名字长度不能超过200字符")
    private String name;

    @ApiModelProperty(value = "排序 默认0 (从大到小排)")
    private Integer groupSort;

    @ApiModelProperty(value = "患者ID(英文 , 分隔)")
    private String patientIdStrings;

    @ApiModelProperty(value = "患者ID")
    private List<Long> patientIds;

}
