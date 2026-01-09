package com.caring.sass.file.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 视频截图任务
 * </p>
 *
 * @author 杨帅
 * @since 2023-04-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ScreenshotTaskSaveDTO", description = "视频截图任务")
public class ScreenshotTaskSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "OBS的bucket名称")
    private String bucket;


    @ApiModelProperty(value = "OBS对象路径，遵守OSS Object定义")
    private String object;

    @ApiModelProperty(value = "OBS对象的名称")
    private String fileName;


}
