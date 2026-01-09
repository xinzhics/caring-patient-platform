package com.caring.sass.ai.entity.podcast;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_podcast")
@ApiModel(value = "Podcast", description = "播客")
@AllArgsConstructor
public class Podcast extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 制作的步骤
     */
    @ApiModelProperty(value = "制作的步骤 1  2  3  4")
    @TableField("step")
    @Excel(name = "制作的步骤")
    private Integer step;

    /**
     * 任务状态 生成对话，制作音频
     */
    @ApiModelProperty(value = "任务状态 生成对话，制作音频")
    @Length(max = 100, message = "任务状态 生成对话，制作音频长度不能超过100")
    @TableField(value = "task_status", condition = LIKE)
    @Excel(name = "任务状态 生成对话，制作音频")
    private PodcastTaskStatus taskStatus;

    /**
     * 播客名称
     */
    @ApiModelProperty(value = "播客名称")
    @Length(max = 20, message = "播客名称长度不能超过20")
    @TableField(value = "podcast_name", condition = LIKE)
    @Excel(name = "播客名称")
    private String podcastName;

    /**
     * 播客素材类型 文章链接，文档，文字输入
     */
    @ApiModelProperty(value = "播客素材类型 网页，文件，文本")
    @Length(max = 20, message = "播客素材类型 文章链接，文档，文字输入长度不能超过20")
    @TableField(value = "podcast_input_type", condition = LIKE)
    @Excel(name = "播客素材类型 文章链接，文档，文字输入")
    private PodcastInputType podcastInputType;

    /**
     * 文章链接或文档url
     */
    @ApiModelProperty(value = "文章链接或文档url")
    @Length(max = 200, message = "文章链接或文档url长度不能超过200")
    @TableField(value = "podcast_input_content", condition = LIKE)
    @Excel(name = "文章链接或文档url")
    private String podcastInputContent;

    /**
     * 文字内容
     */
    @ApiModelProperty(value = "文字内容")
    @TableField("podcast_input_text_content")
    @Excel(name = "文字内容")
    private String podcastInputTextContent;

    /**
     * 文档的文件名称
     */
    @ApiModelProperty(value = "文档的文件名称")
    @Length(max = 20, message = "文档的文件名称长度不能超过20")
    @TableField(value = "podcast_input_file_name", condition = LIKE)
    @Excel(name = "文档的文件名称")
    private String podcastInputFileName;

    /**
     * 文档的文件大小
     */
    @ApiModelProperty(value = "文档的文件大小")
    @Length(max = 200, message = "文档的文件大小长度不能超过20")
    @TableField(value = "podcast_input_file_size", condition = LIKE)
    @Excel(name = "文档的文件大小")
    private Integer podcastInputFileSize;

    /**
     * 播客风格
     */
    @ApiModelProperty(value = "播客风格")
    @Length(max = 20, message = "播客风格长度不能超过20")
    @TableField(value = "podcast_style", condition = LIKE)
    @Excel(name = "播客风格")
    private PodcastStyle podcastStyle;

    /**
     * 预览内容
     */
    @ApiModelProperty(value = "预览内容")
    @TableField("podcast_preview_contents")
    @Excel(name = "预览内容")
    private String podcastPreviewContents;

    /**
     * 播客最终音频地址
     */
    @ApiModelProperty(value = "播客最终音频地址")
    @TableField("podcast_final_audio_url")
    @Excel(name = "播客最终音频地址")
    private String podcastFinalAudioUrl;

    /**
     * 音频任务总数
     */
    @ApiModelProperty(value = "音频任务总数")
    @NotNull(message = "音频任务总数不能为空")
    @TableField("podcast_audio_task_number")
    @Excel(name = "音频任务总数")
    private Integer podcastAudioTaskNumber;


    @ApiModelProperty(value = "合并音频状态")
    @TableField("merge_audio_status")
    @Excel(name = "合并音频状态")
    private String mergeAudioStatus;

    @ApiModelProperty(value = "合并音频失败信息")
    @TableField("merge_audio_info")
    @Excel(name = "合并音频失败信息")
    private String mergeAudioInfo;


    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "所属用户ID")
    @NotNull(message = "所属用户ID不能为空")
    @TableField("user_id")
    @Excel(name = "所属用户ID")
    private Long userId;


    @ApiModelProperty(value = "播客类型 科普创作： article, 文献解读： textual,  易智声 yi_zhisheng ")
    @TableField("podcast_type")
    @Excel(name = "播客类型")
    private String podcastType;

    @ApiModelProperty(value = "播客模式")
    @TableField("podcast_model")
    private PodcastModel podcastModel;

    @ApiModelProperty(value = "播客语言")
    @TableField("podcast_language")
    private PodcastLanguage podcastLanguage;

    @ApiModelProperty(value = "是否发布")
    @TableField("release_status")
    private Boolean releaseStatus;

    @ApiModelProperty(value = "展示数量")
    @TableField("show_number")
    private Integer showNumber;

    @ApiModelProperty(value = "是否展示到docuKnow")
    @TableField("show_ai_knowledge")
    private Boolean showAIKnowledge;

    @ApiModelProperty(value = "失败退款")
    @TableField(value = "fail_refund")
    private Boolean failRefund;

    @ApiModelProperty(value = "设置的声音")
    @TableField(exist = false)
    private List<PodcastSoundSet> soundSet;

}
