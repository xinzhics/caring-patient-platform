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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 音频转录数据更新DTO
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
@ApiModel(value = "AudioAnalysisTaskTranscriptUpdateDTO", description = "音频转录数据更新DTO")
public class AudioAnalysisTaskTranscriptUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键ID
     */
    @ApiModelProperty(value = "数据库主键ID", required = true)
    @NotNull(message = "任务ID不能为空")
    private Long id;

    /**
     * 转录文本数据(JSON格式)
     */
    @ApiModelProperty(value = "转录文本数据(JSON格式)", required = true)
    @org.hibernate.validator.constraints.NotEmpty(message = "转录数据不能为空")
    private String transcriptData;

    /**
     * 解析步骤(固定为2-转录完成)
     */
    @ApiModelProperty(value = "解析步骤(固定为2-转录完成)")
    @Builder.Default
    private Integer step = 2;
}
