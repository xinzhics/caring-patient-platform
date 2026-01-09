package com.caring.sass.ai.entity.know;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;


@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_system_file")
@ApiModel(value = "KnowledgeSystemFile", description = "知识库系统文件")
@AllArgsConstructor
public class KnowledgeSystemFile extends Entity<Long> {

    private static final long serialVersionUID = 1L;


    /**
     * 知识库dify的Id
     */
    @ApiModelProperty(value = "知识库dify的Id")
    @Length(max = 50, message = "知识库dify的Id长度不能超过50")
    @TableField(value = "know_dify_id", condition = LIKE)
    @Excel(name = "知识库dify的Id")
    private String knowDifyId;


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


}
