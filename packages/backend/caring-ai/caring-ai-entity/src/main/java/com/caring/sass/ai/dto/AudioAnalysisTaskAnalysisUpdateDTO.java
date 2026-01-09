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
 * AI解析数据更新DTO
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
@ApiModel(value = "AudioAnalysisTaskAnalysisUpdateDTO", description = "AI解析数据更新DTO")
public class AudioAnalysisTaskAnalysisUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键ID
     */
    @ApiModelProperty(value = "数据库主键ID", required = true)
    @NotNull(message = "任务ID不能为空")
    private Long id;

    /**
     * AI总结数据(JSON格式,包含章节、要点、思维导图等)
     */
    @ApiModelProperty(value = "AI总结数据(JSON格式)", required = true)
    @org.hibernate.validator.constraints.NotEmpty(message = "AI总结数据不能为空")
    private String summaryData;

    /**
     * 思维导图数据(JSON格式)
     */
    @ApiModelProperty(value = "思维导图数据(JSON格式)")
    private String mindmapData;

    /**
     * 解析步骤(固定为3-AI解析完成)
     */
    @ApiModelProperty(value = "解析步骤(固定为3-AI解析完成)")
    @Builder.Default
    private Integer step = 3;

    /**
     * 任务状态(1-解析完成)
     */
    @ApiModelProperty(value = "任务状态(1-解析完成)")
    @Builder.Default
    private Integer taskStatus = 1;
}
