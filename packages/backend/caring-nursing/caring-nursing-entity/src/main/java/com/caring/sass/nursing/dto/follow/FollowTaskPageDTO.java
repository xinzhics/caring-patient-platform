package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 随访任务
 * </p>
 *
 * @author 杨帅
 * @since 2023-01-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FollowTaskPageDTO", description = "随访任务")
public class FollowTaskPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "名称")
    @Length(max = 20, message = "长度不能超过20")
    private String name;

    @ApiModelProperty(value = "背景图")
    @Length(max = 255, message = "长度不能超过255")
    private String url;
    /**
     * 是否显示概要（0：不显示  1：显示）
     */
    @ApiModelProperty(value = "是否显示概要（0：不显示  1：显示）")
    private Integer showOutline;

}
