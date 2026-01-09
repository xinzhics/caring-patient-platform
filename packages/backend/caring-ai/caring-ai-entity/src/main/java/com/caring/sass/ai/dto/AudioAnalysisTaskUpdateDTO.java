package com.caring.sass.ai.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 音频解析任务表
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
@ApiModel(value = "AudioAnalysisTaskUpdateDTO", description = "音频解析任务表")
public class AudioAnalysisTaskUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 任务唯一标识
     */
    @ApiModelProperty(value = "任务唯一标识")
    @Length(max = 64, message = "任务唯一标识长度不能超过64")
    private String taskId;
    /**
     * 解析步骤 1-上传音频 
     * 	2-转录完成 3-AI解析完成
     */
    @ApiModelProperty(value = "解析步骤 1-上传音频")
    private Integer step;
    /**
     * 上传音频文件名称
     */
    @ApiModelProperty(value = "上传音频文件名称")
    @Length(max = 500, message = "上传音频文件名称长度不能超过500")
    private String fileName;
    /**
     * 音频标题
     */
    @ApiModelProperty(value = "音频标题")
    @Length(max = 300, message = "音频标题长度不能超过300")
    private String title;
    /**
     * 任务状态 0-解析中
     * 	1-解析完成 2-存为草稿 3-解析失败
     */
    @ApiModelProperty(value = "任务状态 0-解析中")
    private Integer taskStatus;
    /**
     * 音频文件URL
     */
    @ApiModelProperty(value = "音频文件URL")
    @Length(max = 500, message = "音频文件URL长度不能超过500")
    private String audioUrl;
    /**
     * 音频时长(秒)
     */
    @ApiModelProperty(value = "音频时长(秒)")
    private Integer audioDuration;
    /**
     * 转录文本数据(JSON格式)
     */
    @ApiModelProperty(value = "转录文本数据(JSON格式)")
    private String transcriptData;
    /**
     * AI总结数据(JSON格式,包含章节、要点、思维导图等)
     */
    @ApiModelProperty(value = "AI总结数据(JSON格式,包含章节、要点、思维导图等)")
    private String summaryData;
    /**
     * 思维导图数据(JSON格式)
     */
    @ApiModelProperty(value = "思维导图数据(JSON格式)")
    private String mindmapData;
    /**
     * 问答历史记录(JSON格式)
     */
    @ApiModelProperty(value = "问答历史记录(JSON格式)")
    private String chatHistory;
    /**
     * 关键词(逗号分隔)
     */
    @ApiModelProperty(value = "关键词(逗号分隔)")
    @Length(max = 500, message = "关键词(逗号分隔)长度不能超过500")
    private String keywords;
    /**
     * 音频分类
     */
    @ApiModelProperty(value = "音频分类")
    @Length(max = 100, message = "音频分类长度不能超过100")
    private String category;
    /**
     * 音频语言
     */
    @ApiModelProperty(value = "音频语言")
    @Length(max = 20, message = "音频语言长度不能超过20")
    private String language;
    /**
     * 使用的AI模型
     */
    @ApiModelProperty(value = "使用的AI模型")
    @Length(max = 100, message = "使用的AI模型长度不能超过100")
    private String aiModel;
    /**
     * 文件在coze的ID
     */
    @ApiModelProperty(value = "文件在coze的ID")
    @Length(max = 255, message = "文件在coze的ID长度不能超过255")
    private String cozeFileId;
    /**
     * 会话ID
     */
    @ApiModelProperty(value = "会话ID")
    @Length(max = 255, message = "会话ID长度不能超过255")
    private String uid;
    /**
     * 是否展示到AI知识平台 0-否 1-是
     */
    @ApiModelProperty(value = "是否展示到AI知识平台 0-否 1-是")
    private Integer showAiKnowledge;
    /**
     * 是否公开 0-私有 
     * 	1-公开
     */
    @ApiModelProperty(value = "是否公开 0-私有")
    private Integer isPublic;
    /**
     * 查看次数
     */
    @ApiModelProperty(value = "查看次数")
    private Integer viewCount;
    /**
     * 点赞次数
     */
    @ApiModelProperty(value = "点赞次数")
    private Integer likeCount;
    /**
     * 最后一次AI解析时间
     */
    @ApiModelProperty(value = "最后一次AI解析时间")
    private LocalDateTime lastAnalysisTime;
}
