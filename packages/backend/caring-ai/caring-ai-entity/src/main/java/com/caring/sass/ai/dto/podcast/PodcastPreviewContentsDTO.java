package com.caring.sass.ai.dto.podcast;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 播客
 * </p>
 *
 * @author 杨帅
 * @since 2024-11-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PodcastPreviewContentsDTO", description = "播客")
public class PodcastPreviewContentsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "播客id")
    @NotNull
    private Long podcastId;

    /**
     * 预览内容
     */
    @ApiModelProperty(value = "预览对话内容")
    private String podcastPreviewContents;

}
