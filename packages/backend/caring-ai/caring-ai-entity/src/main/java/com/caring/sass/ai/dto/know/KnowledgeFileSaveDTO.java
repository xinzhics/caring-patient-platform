package com.caring.sass.ai.dto.know;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

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
@ApiModel(value = "KnowledgeFileSaveDTO", description = "dify知识库文档关联表")
public class KnowledgeFileSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;


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


    @ApiModelProperty(value = "文件网盘url")
    @NotNull
    @NotEmpty
    @Length(max = 300)
    private String fileUrl;


    @Length(max = 300)
    @ApiModelProperty(value = "视频封面")
    private String fileCover;



}
