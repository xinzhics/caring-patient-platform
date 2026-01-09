package com.caring.sass.ai.entity.humanVideo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
 * 用户声音结果
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-14
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_user_audio_result")
@ApiModel(value = "BusinessUserAudioResult", description = "用户声音结果")
@AllArgsConstructor
public class BusinessUserAudioResult extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 音色ID
     */
    @ApiModelProperty(value = "音色ID")
    @Length(max = 255, message = "音色ID长度不能超过255")
    @TableField(value = "timbre_id", condition = LIKE)
    @Excel(name = "音色ID")
    private String timbreId;

    /**
     * 音频文字内容
     */
    @ApiModelProperty(value = "音频文字内容")
    @Length(max = 500, message = "音频文字内容长度不能超过500")
    @TableField(value = "text_content", condition = LIKE)
    @Excel(name = "音频文字内容")
    private String textContent;

    /**
     * 语音文件url
     */
    @ApiModelProperty(value = "语音文件url")
    @Length(max = 255, message = "语音文件url长度不能超过255")
    @TableField(value = "file_url", condition = LIKE)
    @Excel(name = "语音文件url")
    private String fileUrl;

    /**
     * 合成状态
     */
    @ApiModelProperty(value = "合成状态")
    @Length(max = 255, message = "合成状态长度不能超过255")
    @TableField(value = "api_status", condition = LIKE)
    @Excel(name = "合成状态")
    private String apiStatus;

    /**
     * 合成异常备注
     */
    @ApiModelProperty(value = "合成异常备注")
    @Length(max = 500, message = "合成异常备注长度不能超过500")
    @TableField(value = "error_message", condition = LIKE)
    @Excel(name = "合成异常备注")
    private String errorMessage;

    /**
     * 制作数字人视频任务ID
     */
    @ApiModelProperty(value = "制作数字人视频任务ID")
    @TableField("video_task_id")
    @Excel(name = "制作数字人视频任务ID")
    private Long videoTaskId;


    @Builder
    public BusinessUserAudioResult(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String timbreId, String textContent, String fileUrl, String apiStatus, String errorMessage, Long videoTaskId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.timbreId = timbreId;
        this.textContent = textContent;
        this.fileUrl = fileUrl;
        this.apiStatus = apiStatus;
        this.errorMessage = errorMessage;
        this.videoTaskId = videoTaskId;
    }

}
