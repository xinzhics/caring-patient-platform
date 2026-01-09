package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 火山方案视频任务
 * </p>
 *
 * @author 杨帅
 * @since 2025-08-13
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_article_volcengine_voice_task")
@ApiModel(value = "ArticleVolcengineVoiceTask", description = "火山方案视频任务")
@AllArgsConstructor
public class ArticleVolcengineVoiceTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 视频创作原任务id
     */
    @ApiModelProperty(value = "视频创作原任务id")
    @TableField("voice_task_id")
    @Excel(name = "视频创作原任务id")
    private Long voiceTaskId;

    /**
     * 视频底板id
     */
    @ApiModelProperty(value = "视频底板id")
    @TableField("template_id")
    @Excel(name = "视频底板id")
    private Long templateId;

    /**
     * 音频的URL
     */
    @ApiModelProperty(value = "音频的URL")
    @Length(max = 2000, message = "音频的URL长度不能超过2000")
    @TableField(value = "audio_url", condition = LIKE)
    @Excel(name = "音频的URL")
    private String audioUrl;

    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态 WAIT_IMAGE 等待做火山形象， WAIT_VIDEO 等待做火山视频")
    @Length(max = 255, message = "任务状态长度不能超过255")
    @TableField(value = "task_status", condition = LIKE)
    @Excel(name = "任务状态")
    private HumanVideoTaskStatus taskStatus;

    /**
     * 火山视频制作任务id
     */
    @ApiModelProperty(value = "火山视频制作任务id")
    @Length(max = 255, message = "火山视频制作任务id长度不能超过255")
    @TableField(value = "volcengine_task_id", condition = LIKE)
    @Excel(name = "火山视频制作任务id")
    private String volcengineTaskId;

    /**
     * 火山视频制作任务结果
     */
    @ApiModelProperty(value = "火山视频制作任务结果")
    @Length(max = 2000, message = "火山视频制作任务结果长度不能超过2000")
    @TableField(value = "volcengine_task_result", condition = LIKE)
    @Excel(name = "火山视频制作任务结果")
    private String volcengineTaskResult;

    /**
     * 形象类型 1 图片 2 视频
     */
    @ApiModelProperty(value = "形象类型 1 图片 2 视频")
    @TableField("template_type")
    @Excel(name = "形象类型 1 图片 2 视频")
    private Integer templateType;

    /**
     * 异常信息
     */
    @ApiModelProperty(value = "异常信息")
    @TableField("volcengine_task_error_message")
    @Excel(name = "异常信息")
    private String volcengineTaskErrorMessage;


}
