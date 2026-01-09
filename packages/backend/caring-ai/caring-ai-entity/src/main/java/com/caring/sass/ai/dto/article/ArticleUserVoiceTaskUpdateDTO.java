package com.caring.sass.ai.dto.article;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * 声音管理
 * </p>
 *
 * @author leizhi
 * @since 2025-02-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleUserVoiceTaskUpdateDTO", description = "声音管理")
public class ArticleUserVoiceTaskUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;
    /**
     * 音色id
     */
    @ApiModelProperty(value = "音色id")
    private Long voiceId;
    /**
     * 形象id
     */
    @ApiModelProperty(value = "形象id")
    private Long avatarId;
    /**
     * 讲述内容
     */
    @ApiModelProperty(value = "讲述内容")
    @Length(max = 65535, message = "讲述内容长度不能超过65,535")
    private String talkText;
    /**
     * 复刻制作的音频文件链接
     */
    @ApiModelProperty(value = "复刻制作的音频文件链接")
    @Length(max = 500, message = "复刻制作的音频文件链接长度不能超过500")
    private String generateAudioUrl;
    /**
     * 上传的音频文件
     */
    @ApiModelProperty(value = "上传的音频文件")
    @Length(max = 500, message = "上传的音频文件长度不能超过500")
    private String uploadAudioUrl;
    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    @Length(max = 255, message = "任务状态长度不能超过255")
    private String taskStatus;

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    private String taskId;

    private String taskName;

    private String taskType;
}
