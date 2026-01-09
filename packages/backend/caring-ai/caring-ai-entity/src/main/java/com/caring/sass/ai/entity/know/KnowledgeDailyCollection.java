package com.caring.sass.ai.entity.know;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.List;

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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 日常收集文本内容
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-20
 */
@Data
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_daily_collection")
@ApiModel(value = "KnowledgeDailyCollection", description = "日常收集文本内容")
@AllArgsConstructor
public class KnowledgeDailyCollection extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 业务知识库id
     */
    @ApiModelProperty(value = "业务知识库id")
    @Length(max = 20, message = "业务知识库id长度不能超过20")
    @TableField(value = "know_id", condition = EQUAL)
    private Long knowId;

    @ApiModelProperty(value = "dify知识库id")
    @TableField(value = "know_dify_id", condition = EQUAL)
    private String knowDifyId;


    @ApiModelProperty(value = "dify文档id")
    @TableField(value = "document_id", condition = EQUAL)
    private String documentId;

    /**
     * 语音文本标题
     */
    @ApiModelProperty(value = "语音文本标题")
    @Length(max = 200, message = "语音文本标题长度不能超过200")
    @TableField(value = "text_title", condition = LIKE)
    @Excel(name = "语音文本标题")
    private String textTitle;

    /**
     * 语音文本内容
     */
    @ApiModelProperty(value = "语音文本内容")
    @TableField("text_content")
    @Excel(name = "语音文本内容")
    private String textContent;

    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "所属用户ID")
    @TableField("user_id")
    @Excel(name = "所属用户ID")
    private Long userId;

    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    @TableField("file_upload_time")
    @Excel(name = "上传时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime fileUploadTime;


    @ApiModelProperty(value = "语音文件的路径")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty(value = "录音时长")
    @TableField("audio_duration")
    private Integer audioDuration;


    /**
     * 文档状态 启用 禁用
     */
    @ApiModelProperty(value = "文档状态 启用 禁用")
    @Length(max = 50, message = "文档状态 启用 禁用长度不能超过50")
    @TableField(value = "dify_file_status", condition = LIKE)
    @Excel(name = "文档状态 waiting 等待中  completed 已完成 ")
    private String difyFileStatus;

    /**
     * 文档索引进度
     */
    @ApiModelProperty(value = "文档索引进度")
    @Length(max = 50, message = "文档索引进度长度不能超过50")
    @TableField(value = "dify_file_index_progress", condition = LIKE)
    @Excel(name = "文档索引进度")
    private String difyFileIndexProgress;

    /**
     * dify文档的批次号batch
     */
    @ApiModelProperty(value = "dify文档的批次号batch")
    @Length(max = 50, message = "dify文档的批次号batch长度不能超过50")
    @TableField(value = "dify_batch", condition = LIKE)
    @Excel(name = "dify文档的批次号batch")
    private String difyBatch;

    @ApiModelProperty(value = "关键词")
    @TableField(value = "key_word", condition = LIKE)
    private String keyWord;

    @ApiModelProperty(value = "文档权限是否自定义更新")
    @TableField(value = "is_update_permissions", condition = EQUAL)
    private Boolean isUpdatePermissions;


    @ApiModelProperty(value = "查询的等级要求")
    @TableField("view_permissions")
    @Deprecated
    private Integer viewPermissions;

    @ApiModelProperty(value = "病例录音内容")
    @TableField("case_recording_dialogue_text")
    private String caseRecordingDialogueText;

    @ApiModelProperty(value = "数据类型(1 正常日常收集， 2 病历录音内容)")
    @TableField("data_type")
    private Integer dataType;

    @ApiModelProperty(value = "下载的等级要求")
    @TableField("download_permission")
    @Deprecated
    private Integer downloadPermission;
}
