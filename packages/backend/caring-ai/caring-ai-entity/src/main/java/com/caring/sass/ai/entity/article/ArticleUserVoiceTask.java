package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;


@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_article_user_voice_task")
@ApiModel(value = "ArticleUserVoiceTask", description = "数字人视频制作任务")
@AllArgsConstructor
public class ArticleUserVoiceTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @TableField(value = "user_id", condition = EQUAL)
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 音色id
     */
    @ApiModelProperty(value = "音色id")
    @TableField("voice_id")
    @Excel(name = "音色id")
    private Long voiceId;


    /**
     * 讲述内容
     */
    @ApiModelProperty(value = "讲述内容")
    @Length(max = 65535, message = "讲述内容长度不能超过65535")
    @TableField("talk_text")
    @Excel(name = "讲述内容")
    private String talkText;

    /**
     * 复刻制作的音频文件链接
     */
    @ApiModelProperty(value = "数字人链接")
    @TableField(value = "generate_audio_url", condition = LIKE)
    private String generateAudioUrl;


    @ApiModelProperty(value = "视频封面")
    @TableField(value = "human_video_cover", condition = LIKE)
    private String humanVideoCover;

    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    @Length(max = 255, message = "任务状态长度不能超过255")
    @TableField(value = "task_status", condition = LIKE)
    @Excel(name = "任务状态")
    private HumanVideoTaskStatus taskStatus;

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    @Length(max = 255, message = "任务id长度不能超过255")
    @TableField(value = "task_id", condition = LIKE)
    @Excel(name = "任务id")
    private String taskId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @Length(max = 255, message = "任务名称长度不能超过255")
    @TableField(value = "task_name", condition = LIKE)
    @Excel(name = "任务名称")
    private String taskName;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型 图片数字人 对口型数字人")
    @Length(max = 255, message = "任务类型长度不能超过255")
    @TableField(value = "task_type", condition = LIKE)
    @Excel(name = "任务类型")
    private String taskType;


    @ApiModelProperty("形象类型 1 图片 2 视频")
    @TableField(value = "template_type")
    private Integer templateType;

    /**
     * 视频底板id
     */
    @ApiModelProperty(value = "形象ID")
    @TableField(value = "template_id")
    private Long templateId;

    @ApiModelProperty("1 文本， 2 本地音频， 3 我的博客")
    @TableField(value = "create_type", condition = EQUAL)
    private Integer createType;


    @ApiModelProperty("音频的url")
    @TableField(value = "audio_url")
    private String audioUrl;

    @ApiModelProperty("异常信息")
    @TableField(value = "error_message")
    private String errorMessage;


    @ApiModelProperty("制作视频时间")
    @TableField(value = "make_video_time")
    private LocalDateTime makeVideoTime;

    @ApiModelProperty("开启火山任务")
    @TableField(value = "open_volcengine_take")
    private Boolean openVolcengineTake;

    @ApiModelProperty("播客ID")
    @TableField(value = "podcast_id")
    private Long podcastId;

    @ApiModelProperty(value = "展示在ai医生数字分身平台")
    @TableField(value = "show_ai_knowledge", condition = EQUAL)
    private Boolean showAIKnowledge;

    @ApiModelProperty(value = "是否等待合并封面")
    @TableField(value = "wait_merge_cover", condition = EQUAL)
    private Boolean waitMergeCover;

    @ApiModelProperty("封面视频")
    @TableField(value = "cover_video")
    private String coverVideo;

    @ApiModelProperty("后封面视频")
    @TableField(value = "back_cover_video")
    private String backCoverVideo;

    @ApiModelProperty("最终视频结果")
    @TableField(value = "final_video_result")
    private String finalVideoResult;

    @ApiModelProperty(value = "最终视频结果封面")
    @TableField(value = "final_video_result_cover", condition = LIKE)
    private String finalVideoResultCover;

    @ApiModelProperty(value = "失败退款")
    @TableField(value = "fail_refund")
    private Boolean failRefund;

    public void setErrorMessage(String errorMessage) {
        if (errorMessage != null && errorMessage.length() > 60000) {
            errorMessage = errorMessage.substring(0, 60000);
        }
        this.errorMessage = errorMessage;
    }
}
