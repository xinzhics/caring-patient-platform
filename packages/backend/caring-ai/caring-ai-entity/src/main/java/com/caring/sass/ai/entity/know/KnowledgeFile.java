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
 * dify知识库文档关联表
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-20
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_file")
@ApiModel(value = "KnowledgeFile", description = "dify知识库文档关联表")
@AllArgsConstructor
public class KnowledgeFile extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static String DOCUMENT_BELONGS_SYSTEM = "system";
    public static String DOCUMENT_BELONGS_USER = "user";

    /**
     * 业务知识库id
     */
    @ApiModelProperty(value = "业务知识库id")
    @Length(max = 20, message = "业务知识库id长度不能超过20")
    @TableField(value = "know_id", condition = EQUAL)
    private Long knowId;

    /**
     * 知识库dify的Id
     */
    @ApiModelProperty(value = "知识库dify的Id")
    @Length(max = 50, message = "知识库dify的Id长度不能超过50")
    @TableField(value = "know_dify_id", condition = LIKE)
    @Excel(name = "知识库dify的Id")
    private String knowDifyId;


    @ApiModelProperty(value = "文件类型")
    @Length(max = 50)
    @TableField(value = "file_type", condition = EQUAL)
    private KnowFileType fileType;


    @ApiModelProperty(value = "视频封面")
    @Length(max = 233)
    @TableField(value = "file_cover", condition = EQUAL)
    private String fileCover;


    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "所属用户ID")
    @TableField("file_user_id")
    @Excel(name = "所属用户ID")
    private Long fileUserId;

    /**
     * 知识类型(专业学术资料，个人成果，病例库，日常收集)
     */
    @ApiModelProperty(value = "知识类型(专业学术资料，个人成果，病例库，日常收集)")
    @Length(max = 50, message = "知识类型(专业学术资料，个人成果，病例库，日常收集)长度不能超过50")
    @TableField(value = "know_type", condition = LIKE)
    @Excel(name = "知识类型(专业学术资料，个人成果，病例库，日常收集)")
    private KnowledgeType knowType;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    @Length(max = 300, message = "文件名称长度不能超过300")
    @TableField(value = "file_name", condition = LIKE)
    @Excel(name = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "原文件名称")
    @TableField(value = "initial_file_name", condition = LIKE)
    private String initialFileName;

    /**
     * 文件大小(kb)
     */
    @ApiModelProperty(value = "文件大小(kb)")
    @TableField("file_size")
    @Excel(name = "文件大小(kb)")
    private Long fileSize;


    @ApiModelProperty(value = "文件网盘url")
    @TableField("file_url")
    private String fileUrl;

    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    @TableField("file_upload_time")
    @Excel(name = "上传时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime fileUploadTime;

    /**
     * 文档状态 启用 禁用
     */
    @ApiModelProperty(value = "文档状态 waiting 等待中； analysis 分析中； check 已完成； checked 完成")
    @Length(max = 50, message = "文档状态 启用 禁用长度不能超过50")
    @TableField(value = "dify_file_status", condition = LIKE)
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

    /**
     * dify的文档ID
     */
    @ApiModelProperty(value = "dify的文档ID")
    @Length(max = 100, message = "dify的文档ID长度不能超过100")
    @TableField(value = "dify_file_id", condition = LIKE)
    @Excel(name = "dify的文档ID")
    private String difyFileId;


    @ApiModelProperty(value = "文档的所属 system 系统； user 用户的")
    @TableField(value = "document_belongs", condition = EQUAL)
    private String documentBelongs;

    @ApiModelProperty(value = "文档权限是否自定义更新")
    @TableField(value = "is_update_permissions", condition = EQUAL)
    private Boolean isUpdatePermissions;

    @ApiModelProperty(value = "查询的等级要求")
    @TableField("view_permissions")
    private Integer viewPermissions;

    @ApiModelProperty(value = "下载的等级要求")
    @TableField("download_permission")
    private Integer downloadPermission;

    @ApiModelProperty(value = "重试次数")
    @TableField("try_count")
    private Integer tryCount;

    @ApiModelProperty(value = "系统文件ID")
    @TableField("system_file_id")
    private Long systemFileId;

    @ApiModelProperty(value = "是否设置元数据")
    @TableField("set_metadata")
    private Boolean setMetadata;


    @ApiModelProperty(value = "病例库的标签")
    @TableField(exist = false)
    KnowledgeFileCaseDatabaseLabel caseDatabaseLabel;

    @ApiModelProperty(value = "个人成长的标签")
    @TableField(exist = false)
    KnowledgeFilePersonalAchievementsLabel personalAchievementsLabel;

    @ApiModelProperty(value = "专业学术资料标签")
    @TableField(exist = false)
    KnowledgeFileAcademicMaterialsLabel academicMaterialsLabel;



}
