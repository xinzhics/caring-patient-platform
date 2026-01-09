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
@ApiModel(value = "BusinessDigitalHumanVideoTaskSaveDTO", description = "数字人视频制作任务")
public class BusinessDigitalHumanVideoTaskSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 照片数字人url
     */
    @ApiModelProperty(value = "照片数字人url")
    @Length(max = 255, message = "照片数字人url长度不能超过255")
    private String photoHumanUrl;

}
