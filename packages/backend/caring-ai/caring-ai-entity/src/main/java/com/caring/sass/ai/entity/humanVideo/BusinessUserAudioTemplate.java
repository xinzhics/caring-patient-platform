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
 * 用户提交的录音素材
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
@TableName("m_ai_business_user_audio_template")
@ApiModel(value = "BusinessUserAudioTemplate", description = "用户提交的录音素材")
@AllArgsConstructor
public class BusinessUserAudioTemplate extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件的url
     */
    @ApiModelProperty(value = "文件的url")
    @Length(max = 255, message = "文件的url长度不能超过255")
    @TableField(value = "file_url", condition = LIKE)
    @Excel(name = "文件的url")
    private String fileUrl;

    /**
     * 任务状态(等待克隆， 克隆完成， 克隆失败)
     */
    @ApiModelProperty(value = "任务状态(等待克隆， 克隆完成， 克隆失败)")
    @TableField(value = "task_status", condition = LIKE)
    @Excel(name = "任务状态(等待克隆， 克隆完成， 克隆失败)")
    private HumanAudioTaskStatus taskStatus;

    /**
     * 克隆信息
     */
    @ApiModelProperty(value = "克隆失败的信息")
    @TableField("task_message")
    @Excel(name = "克隆信息")
    private String taskMessage;

    /**
     * 克隆使用的音色
     */
    @ApiModelProperty(value = "克隆使用的音色")
    @Length(max = 255, message = "克隆使用的音色长度不能超过255")
    @TableField(value = "timbre_id", condition = LIKE)
    @Excel(name = "克隆使用的音色")
    private String timbreId;

    /**
     * 制作数字人视频任务ID
     */
    @ApiModelProperty(value = "制作数字人视频任务ID")
    @TableField("video_task_id")
    @Excel(name = "制作数字人视频任务ID")
    private Long videoTaskId;



    /**
     * 克隆使用的音色
     */
    @ApiModelProperty(value = "账户")
    @TableField(value = "h_account", condition = LIKE)
    private String hAccount;


    @Builder
    public BusinessUserAudioTemplate(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String fileUrl, HumanAudioTaskStatus taskStatus, String taskMessage, String timbreId, Long videoTaskId, String hAccount) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.fileUrl = fileUrl;
        this.taskStatus = taskStatus;
        this.taskMessage = taskMessage;
        this.timbreId = timbreId;
        this.videoTaskId = videoTaskId;
        this.hAccount = hAccount;
    }

}
