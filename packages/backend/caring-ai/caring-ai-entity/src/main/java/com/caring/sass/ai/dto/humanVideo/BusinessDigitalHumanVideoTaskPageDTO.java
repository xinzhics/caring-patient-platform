package com.caring.sass.ai.dto.humanVideo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 数字人视频制作任务
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BusinessDigitalHumanVideoTaskPageDTO", description = "数字人视频制作任务")
public class BusinessDigitalHumanVideoTaskPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    @Length(max = 255, message = "任务状态长度不能超过255")
    private String taskStatus;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @Length(max = 255, message = "任务名称长度不能超过255")
    private String taskName;

}
