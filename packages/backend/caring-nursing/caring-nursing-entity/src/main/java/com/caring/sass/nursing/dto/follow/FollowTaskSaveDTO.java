package com.caring.sass.nursing.dto.follow;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ApiModel(value = "FollowTaskSaveDTO", description = "随访任务")
public class FollowTaskSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "名称")
    @Length(max = 20, message = "长度不能超过20")
    private String name;

    @ApiModelProperty(value = "背景图")
    @Length(max = 255, message = "长度不能超过255")
    private String url;

    @ApiModelProperty(value = "使用默认背景图 (1: 使用默认，0 使用上传的)")
    private Integer useDefault;

    @ApiModelProperty(value = "背景图文件名称")
    private String bgFileName;
    /**
     * 是否显示概要（0：不显示  1：显示）
     */
    @ApiModelProperty(value = "是否显示概要（0：不显示  1：显示）")
    private Integer showOutline;

}
