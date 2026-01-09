package com.caring.sass.ai.entity.docuSearch;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_question_evidence")
@ApiModel(value = "KnowledgeQuestionEvidence", description = "知识库问题证据")
@AllArgsConstructor
public class KnowledgeQuestionEvidence extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id")
    @NotNull(message = "问题id不能为空")
    @TableField("question_id")
    @Excel(name = "问题id")
    private Long questionId;

    @ApiModelProperty(value = "文献名称")
    @TableField(value = "pmid", condition = EQUAL)
    private String pmid;

    /**
     * 文献名称
     */
    @ApiModelProperty(value = "文献名称")
    @Length(max = 1000, message = "文献名称长度不能超过1000")
    @TableField(value = "document_name", condition = LIKE)
    @Excel(name = "文献名称")
    private String documentName;

    /**
     * 发表时间
     */
    @ApiModelProperty(value = "发表时间")
    @Length(max = 20, message = "发表时间长度不能超过20")
    @TableField(value = "publication_time", condition = LIKE)
    @Excel(name = "发表时间")
    private String publicationTime;

    /**
     * 研究类型
     */
    @ApiModelProperty(value = "研究类型")
    @Length(max = 200, message = "研究类型长度不能超过200")
    @TableField(value = "study_type", condition = LIKE)
    @Excel(name = "研究类型")
    private String studyType;

    /**
     * IF值
     */
    @ApiModelProperty(value = "IF值")
    @Length(max = 10, message = "IF值长度不能超过10")
    @TableField(value = "if_value", condition = LIKE)
    @Excel(name = "IF值")
    private String ifValue;

    /**
     * 中科院大类
     */
    @ApiModelProperty(value = "中科院大类")
    @Length(max = 100, message = "中科院大类长度不能超过100")
    @TableField(value = "cas_large_category", condition = LIKE)
    @Excel(name = "中科院大类")
    private String casLargeCategory;


    @ApiModelProperty(value = "中科院大类期刊分区")
    @TableField(value = "cas_category_quartile", condition = LIKE)
    private String casCategoryQuartile;

    /**
     * 中科院小类
     */
    @ApiModelProperty(value = "中科院小类")
    @Length(max = 100, message = "中科院小类长度不能超过100")
    @TableField(value = "cas_subclass", condition = LIKE)
    @Excel(name = "中科院小类")
    private String casSubclass;

    @ApiModelProperty(value = "中科院小类分区")
    @TableField(value = "cas_sub_category_quartile", condition = LIKE)
    private String casSubCategoryQuartile;

    /**
     * JCR分区
     */
    @ApiModelProperty(value = "JCR分区")
    @Length(max = 100, message = "JCR分区长度不能超过100")
    @TableField(value = "jcr_partition", condition = LIKE)
    @Excel(name = "JCR分区")
    private String jcrPartition;

    @ApiModelProperty(value = "国际标准连续出版物号")
    @TableField(value = "issn", condition = EQUAL)
    private String issn;

    @ApiModelProperty(value = "国际标准连续出版物号-电子版")
    @TableField(value = "eissn", condition = EQUAL)
    private String eissn;

    /**
     * 期刊
     */
    @ApiModelProperty(value = "期刊杂志")
    @Length(max = 300, message = "期刊长度不能超过300")
    @TableField(value = "journal", condition = LIKE)
    @Excel(name = "期刊")
    private String journal;

    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    @Length(max = 300, message = "作者长度不能超过300")
    @TableField(value = "author", condition = LIKE)
    @Excel(name = "作者")
    private String author;

    /**
     * 摘要
     */
    @ApiModelProperty(value = "摘要")
    @Length(max = 300, message = "摘要长度不能超过300")
    @TableField(value = "summary_", condition = LIKE)
    @Excel(name = "摘要")
    private String summary;

    /**
     * DOI
     */
    @ApiModelProperty(value = "DOI")
    @Length(max = 300, message = "DOI长度不能超过300")
    @TableField(value = "doi", condition = LIKE)
    @Excel(name = "DOI")
    private String doi;

    /**
     * 原文链接
     */
    @ApiModelProperty(value = "原文链接")
    @Length(max = 300, message = "原文链接长度不能超过300")
    @TableField(value = "file_url", condition = LIKE)
    @Excel(name = "原文链接")
    private String fileUrl;


    @ApiModelProperty(value = "标题的翻译")
    @TableField(value = "translate_title", condition = LIKE)
    private String translateTitle;

    @ApiModelProperty(value = "摘要的翻译")
    @TableField(value = "translate_abstract", condition = LIKE)
    private String translateAbstract;

    @ApiModelProperty(value = "AI总结")
    @TableField(value = "ai_summary", condition = LIKE)
    private String aiSummary;

    @ApiModelProperty(value = "AI总结状态")
    @TableField(value = "ai_summary_status", condition = EQUAL)
    private Boolean aiSummaryStatus;



    public KnowledgeQuestionEvidence() {
    }

    public KnowledgeQuestionEvidence(KnowledgeUserQuestion userQuestion) {

        this.userId = userQuestion.getUserId();
        this.questionId = userQuestion.getId();

    }


}
