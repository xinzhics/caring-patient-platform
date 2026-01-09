package com.caring.sass.ai.dto.docuSearch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
 * 知识库问题证据
 * </p>
 *
 * @author 杨帅
 * @since 2024-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KnowledgeQuestionEvidencePageDTO", description = "知识库问题证据")
public class KnowledgeQuestionEvidencePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;
    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id")
    @NotNull(message = "问题id不能为空")
    private Long questionId;
    /**
     * 文献名称
     */
    @ApiModelProperty(value = "文献名称")
    @Length(max = 1000, message = "文献名称长度不能超过1000")
    private String documentName;
    /**
     * 发表时间
     */
    @ApiModelProperty(value = "发表时间")
    @Length(max = 20, message = "发表时间长度不能超过20")
    private String publicationTime;
    /**
     * 研究类型
     */
    @ApiModelProperty(value = "研究类型")
    @Length(max = 200, message = "研究类型长度不能超过200")
    private String studyType;
    /**
     * IF值
     */
    @ApiModelProperty(value = "IF值")
    @Length(max = 10, message = "IF值长度不能超过10")
    private String ifValue;
    /**
     * 中科院大类
     */
    @ApiModelProperty(value = "中科院大类")
    @Length(max = 100, message = "中科院大类长度不能超过100")
    private String casLargeCategory;
    /**
     * 中科院小类
     */
    @ApiModelProperty(value = "中科院小类")
    @Length(max = 100, message = "中科院小类长度不能超过100")
    private String casSubclass;
    /**
     * JCR分区
     */
    @ApiModelProperty(value = "JCR分区")
    @Length(max = 100, message = "JCR分区长度不能超过100")
    private String jcrPartition;
    /**
     * 期刊
     */
    @ApiModelProperty(value = "期刊")
    @Length(max = 300, message = "期刊长度不能超过300")
    private String journal;
    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    @Length(max = 300, message = "作者长度不能超过300")
    private String author;
    /**
     * 摘要
     */
    @ApiModelProperty(value = "摘要")
    @Length(max = 300, message = "摘要长度不能超过300")
    private String abstract_;
    /**
     * DOI
     */
    @ApiModelProperty(value = "DOI")
    @Length(max = 300, message = "DOI长度不能超过300")
    private String doi;
    /**
     * 原文链接
     */
    @ApiModelProperty(value = "原文链接")
    @Length(max = 300, message = "原文链接长度不能超过300")
    private String fileUrl;

}
