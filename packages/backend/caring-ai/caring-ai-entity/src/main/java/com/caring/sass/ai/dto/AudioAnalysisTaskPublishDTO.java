package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 保存到我的作品DTO
 * </p>
 *
 * @author leizhi
 * @since 2025-12-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "AudioAnalysisTaskPublishDTO", description = "保存到我的作品DTO")
public class AudioAnalysisTaskPublishDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键ID
     */
    @ApiModelProperty(value = "数据库主键ID", required = true)
    @NotNull(message = "任务ID不能为空")
    private Long id;

    /**
     * 作品标题
     */
    @ApiModelProperty(value = "作品标题", required = true)
    @org.hibernate.validator.constraints.NotEmpty(message = "作品标题不能为空")
    @Length(max = 300, message = "作品标题长度不能超过300")
    private String title;

    /**
     * 是否公开 0-私有 1-公开
     */
    @ApiModelProperty(value = "是否公开 0-私有 1-公开")
    @Builder.Default
    private Integer isPublic = 0;

    /**
     * 音频分类
     */
    @ApiModelProperty(value = "音频分类")
    @Length(max = 100, message = "音频分类长度不能超过100")
    private String category;

    /**
     * 关键词(逗号分隔)
     */
    @ApiModelProperty(value = "关键词(逗号分隔)")
    @Length(max = 500, message = "关键词长度不能超过500")
    private String keywords;

    /**
     * 任务状态(2-存为草稿)
     */
    @ApiModelProperty(value = "任务状态(2-存为草稿)")
    @Builder.Default
    private Integer taskStatus = 2;
}
