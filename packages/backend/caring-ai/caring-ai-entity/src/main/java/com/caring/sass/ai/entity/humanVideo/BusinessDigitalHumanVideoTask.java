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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 数字人视频制作任务
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
@TableName("m_ai_business_digital_human_video_task")
@ApiModel(value = "BusinessDigitalHumanVideoTask", description = "数字人视频制作任务")
@AllArgsConstructor
public class BusinessDigitalHumanVideoTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名片ID
     */
    @ApiModelProperty(value = "名片ID")
    @TableField("business_card_id")
    @Excel(name = "名片ID")
    private Long businessCardId;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    @TableField(value = "task_status", condition = LIKE)
    @Excel(name = "任务状态")
    private HumanVideoTaskStatus taskStatus;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @Length(max = 255, message = "任务名称长度不能超过255")
    @TableField(value = "task_name", condition = LIKE)
    @Excel(name = "任务名称")
    private String taskName;

    /**
     * 制作方式(photo：照片, video: 视频)
     */
    @ApiModelProperty(value = "制作方式(photo：照片, video: 视频)")
    @TableField(value = "make_way", condition = LIKE)
    @Excel(name = "制作方式(photo：照片, video: 视频)")
    private HumanVideoMakeWay makeWay;

    /**
     * 照片数字人url
     */
    @ApiModelProperty(value = "照片数字人url")
    @Length(max = 255, message = "照片数字人url长度不能超过255")
    @TableField(value = "photo_human_url", condition = LIKE)
    @Excel(name = "照片数字人url")
    private String photoHumanUrl;

    /**
     * 录音文件url
     */
    @ApiModelProperty(value = "录音文件url")
    @Length(max = 255, message = "录音文件url长度不能超过255")
    @TableField(value = "audio_url", condition = LIKE)
    @Excel(name = "录音文件url")
    private String audioUrl;

    /**
     * 视频内容文案
     */
    @ApiModelProperty(value = "视频内容文案")
    @Length(max = 1000, message = "视频内容文案长度不能超过1000")
    @TableField(value = "video_text_content", condition = LIKE)
    @Excel(name = "视频内容文案")
    private String videoTextContent;



    @ApiModelProperty(value = "是否需要制作音色")
    @TableField(value = "create_timbre")
    private Boolean createTimbre;

    /**
     * 模版视频url
     */
    @ApiModelProperty(value = "百度视频的url")
    @TableField(value = "baidu_video_url", condition = LIKE)
    private String baiduVideoUrl;

    /**
     * 模版视频url
     */
    @ApiModelProperty(value = "模版视频url")
    @Length(max = 255, message = "模版视频url长度不能超过255")
    @TableField(value = "template_video_url", condition = LIKE)
    @Excel(name = "模版视频url")
    private String templateVideoUrl;

    /**
     * 授权视频url
     */
    @ApiModelProperty(value = "授权视频url")
    @Length(max = 255, message = "授权视频url长度不能超过255")
    @TableField(value = "auth_video_url", condition = LIKE)
    @Excel(name = "授权视频url")
    private String authVideoUrl;

    @ApiModelProperty(value = "数字人视频结果URL")
    @TableField(value = "human_video_url", condition = LIKE)
    private String humanVideoUrl;

    @ApiModelProperty(value = "数字人视频封面")
    @TableField(value = "human_video_cover", condition = LIKE)
    private String humanVideoCover;

    @ApiModelProperty(value = "底板视频素材文件 ID,使用文件上传接口上传视频文件获取文件ID")
    @TableField(value = "template_video_id", condition = LIKE)
    private String templateVideoId;

    @ApiModelProperty(value = "底板视频素材宽度")
    @TableField(value = "video_width", condition = EQUAL)
    private Integer videoWidth;

    @ApiModelProperty(value = "底板视频素材高度")
    @TableField(value = "video_height", condition = EQUAL)
    private Integer videoHeight;


    /**
     * 百度调整。模版id会过期。废弃此字段
     */
    @Deprecated()
    @ApiModelProperty(value = "底板 ID，使用 templateVideoId 合成视频后会返回")
    @TableField(value = "template_id", condition = LIKE)
    private String templateId;

    @TableField(value = "baidu_task_id", condition = LIKE)
    private String baiduTaskId;


    @TableField(value = "task_message", condition = LIKE)
    private String taskMessage;

    @ApiModelProperty(value = "任务来源")
    @TableField(value = "task_source", condition = LIKE)
    private HumanTaskSource taskSource;

    @ApiModelProperty(value = "视频开始时间")
    @TableField(value = "start_time", condition = LIKE)
    private LocalDateTime startTime;

    @ApiModelProperty(value = "视频结束时间")
    @TableField(value = "end_time", condition = LIKE)
    private LocalDateTime endTime;


    @Builder
    public BusinessDigitalHumanVideoTask(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long businessCardId, Long userId, HumanVideoTaskStatus taskStatus, String taskName, HumanVideoMakeWay makeWay,
                    String photoHumanUrl, String audioUrl, String videoTextContent, String templateVideoUrl, String baiduTaskId,
                                          String taskMessage, HumanTaskSource taskSource,
                                         String templateId, String templateVideoId, String authVideoUrl, String humanVideoUrl) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.businessCardId = businessCardId;
        this.userId = userId;
        this.taskStatus = taskStatus;
        this.taskName = taskName;
        this.makeWay = makeWay;
        this.photoHumanUrl = photoHumanUrl;
        this.audioUrl = audioUrl;
        this.videoTextContent = videoTextContent;
        this.templateVideoUrl = templateVideoUrl;
        this.authVideoUrl = authVideoUrl;
        this.humanVideoUrl = humanVideoUrl;
        this.templateId = templateId;
        this.templateVideoId = templateVideoId;
        this.baiduTaskId = baiduTaskId;
        this.taskMessage = taskMessage;
        this.taskSource = taskSource;
    }

}
