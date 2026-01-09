package com.caring.sass.ai.dto.know;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
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
 * dify知识库文档关联表
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KnowledgeFilePageDTO", description = "dify知识库文档关联表")
public class KnowledgeFilePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务知识库id
     */
    @ApiModelProperty(value = "业务知识库id")
    @Length(max = 20, message = "业务知识库id长度不能超过20")
    private String knowId;
    /**
     * 知识库dify的Id
     */
    @ApiModelProperty(value = "知识库dify的Id")
    @Length(max = 50, message = "知识库dify的Id长度不能超过50")
    private String knowDifyId;
    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "所属用户ID")
    private Long fileUserId;
    /**
     * 知识类型(专业学术资料，个人成果，病例库，日常收集)
     */
    @ApiModelProperty(value = "知识类型(专业学术资料，个人成果，病例库，日常收集)")
    @Length(max = 50, message = "知识类型(专业学术资料，个人成果，病例库，日常收集)长度不能超过50")
    private String knowType;
    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    @Length(max = 300, message = "文件名称长度不能超过300")
    private String fileName;
    /**
     * 文件大小(kb)
     */
    @ApiModelProperty(value = "文件大小(kb)")
    private Long fileSize;
    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    private LocalDateTime fileUploadTime;
    /**
     * 文档状态 启用 禁用
     */
    @ApiModelProperty(value = "文档状态 启用 禁用")
    @Length(max = 50, message = "文档状态 启用 禁用长度不能超过50")
    private String difyFileStatus;
    /**
     * 文档索引进度
     */
    @ApiModelProperty(value = "文档索引进度")
    @Length(max = 50, message = "文档索引进度长度不能超过50")
    private String difyFileIndexProgress;
    /**
     * dify文档的批次号batch
     */
    @ApiModelProperty(value = "dify文档的批次号batch")
    @Length(max = 50, message = "dify文档的批次号batch长度不能超过50")
    private String difyBatch;
    /**
     * dify的文档ID
     */
    @ApiModelProperty(value = "dify的文档ID")
    @Length(max = 100, message = "dify的文档ID长度不能超过100")
    private String difyFileId;

}
