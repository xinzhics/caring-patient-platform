package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
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

/**
 * <p>
 * 实体类
 * 视频底板
 * </p>
 *
 * @author leizhi
 * @since 2025-02-26
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_article_user_video_template")
@ApiModel(value = "ArticleUserVideoTemplate", description = "数字人形象")
@AllArgsConstructor
public class ArticleUserVideoTemplate extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 图片链接
     */
    @ApiModelProperty(value = "图片链接")
    @Length(max = 500, message = "用户图片链接长度不能超过500")
    @TableField(value = "avatar_url")
    @Excel(name = "用户图片链接")
    private String avatarUrl;


    @ApiModelProperty(value = "类型1 图片, 2视频")
    @TableField(value = "type_", condition = EQUAL)
    private Integer type;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 底板视频素材文件 ID
     */
    @Deprecated
    @ApiModelProperty(value = "底板视频素材文件 ID")
    @Length(max = 255, message = "底板视频素材文件 ID长度不能超过255")
    @TableField(value = "template_video_id", condition = LIKE)
    @Excel(name = "底板视频素材文件 ID")
    private String templateVideoId;

    /**
     * 底板 ID,视频合成速度相较于使用 templateVideoId 合成会快很多,优先使用该参数
     */
    @ApiModelProperty(value = "底板 ID")
    @Length(max = 255, message = "底板 ID长度不能超过255")
    @TableField(value = "template_id", condition = LIKE)
    @Excel(name = "底板 ID")
    private String templateId;

    /**
     * 视频链接
     */
    @ApiModelProperty(value = "视频链接")
    @Length(max = 255, message = "视频链接长度不能超过255")
    @TableField(value = "video_url", condition = LIKE)
    @Excel(name = "视频链接")
    private String videoUrl;

    /**
     * 视频链接
     */
    @ApiModelProperty(value = "授权视频")
    @Length(max = 255, message = "授权视频链接长度不能超过255")
    @TableField(value = "auth_video_url", condition = LIKE)
    private String authVideoUrl;

    /**
     * 底板名称
     */
    @ApiModelProperty(value = "底板名称")
    @Length(max = 255, message = "底板名称长度不能超过255")
    @TableField(value = "video_name", condition = LIKE)
    @Excel(name = "底板名称")
    private String videoName;

    /**
     * 文件原始名称
     */
    @ApiModelProperty(value = "文件原始名称")
    @Length(max = 255, message = "文件原始名称长度不能超过255")
    @TableField(value = "file_name", condition = LIKE)
    @Excel(name = "文件原始名称")
    private String fileName;


    @TableField(value = "video_width")
    private Integer videoWidth;


    @TableField(value = "video_height")
    private Integer videoHeight;


    @ApiModelProperty(value = "使用次数")
    @TableField(value = "use_count")
    private Integer useCount;


    @ApiModelProperty(value = "是否克隆音频")
    @TableField(value = "clone_voice")
    private Boolean cloneVoice;

    @ApiModelProperty(value = "克隆音频状态 false 未处理  true 已处理 ")
    @TableField(value = "clone_voice_status")
    private Boolean cloneVoiceStatus;

    @ApiModelProperty(value = "删除状态")
    @TableField(value = "delete_status")
    private Boolean deleteStatus;

    @ApiModelProperty(value = "火山单图形象任务")
    @TableField(value = "volcengine_image_task_id")
    private String volcengineImageTaskId;

    @ApiModelProperty(value = "火山单图形象结果")
    @TableField(value = "volcengine_image_result")
    private String volcengineImageResult;

    @ApiModelProperty(value = "火山单图形象错误信息")
    @TableField(value = "volcengine_image_error_message")
    private String volcengineImageErrorMessage;


    @ApiModelProperty(value = "水印任务ID")
    @TableField(value = "water_job_id")
    private String waterJobId;

    @ApiModelProperty(value = "水印任务请求ID")
    @TableField(value = "water_job_request_id")
    private String waterJobRequestId;


    @ApiModelProperty(value = "水印任务结果列表")
    @TableField(value = "water_job_result_list_json")
    private String waterJobResultListJson;

    @ApiModelProperty(value = "水印任务开始时间")
    @TableField(value = "water_job_start_time")
    private LocalDateTime waterJobStartTime;

    @ApiModelProperty(value = "水印任务查询结果")
    @TableField(value = "water_job_query_result_json")
    private String waterJobQueryResultJson;


    @ApiModelProperty(value = "原视频文件链接")
    @TableField(value = "file_url_back")
    private String fileUrlBack;


    @ApiModelProperty(value = "水印任务状态")
    @TableField(value = "water_job_status")
    private WatermarkStatus waterJobStatus;




}
