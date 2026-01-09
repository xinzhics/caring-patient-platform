package com.caring.sass.ai.dto.docuSearch;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KnowledgeQuestionEvidenceModel", description = "知识库问题证据")
public class KnowledgeQuestionEvidenceModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id")
    private Long questionId;

    /**
     * 文献名称
     */
    @ApiModelProperty(value = "文献名称")
    private String documentName;


    @ApiModelProperty(value = "文献名称")
    private String pmid;

    /**
     * 发表时间
     */
    @ApiModelProperty(value = "发表时间")
    private String publicationTime;

    /**
     * 研究类型
     */
    @ApiModelProperty(value = "研究类型")
    private String studyType;

    /**
     * IF值
     */
    @ApiModelProperty(value = "IF值")
    private String ifValue;

    /**
     * 中科院大类
     */
    @ApiModelProperty(value = "中科院大类")
    private String casLargeCategory;

    /**
     * 中科院小类
     */
    @ApiModelProperty(value = "中科院小类")
    private String casSubclass;

    @ApiModelProperty(value = "中科院大类期刊分区")
    private String casCategoryQuartile;

    @ApiModelProperty(value = "中科院小类分区")
    private String casSubCategoryQuartile;

    /**
     * JCR分区
     */
    @ApiModelProperty(value = "JCR分区")
    private String jcrPartition;

    @ApiModelProperty(value = "国际标准连续出版物号")
    private String issn;

    @ApiModelProperty(value = "国际标准连续出版物号-电子版")
    private String eissn;

    /**
     * 期刊
     */
    @ApiModelProperty(value = "期刊杂志")
    private String journal;

    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    private String author;

    /**
     * 摘要
     */
    @ApiModelProperty(value = "摘要")
    private String summary;

    /**
     * DOI
     */
    @ApiModelProperty(value = "DOI")
    private String doi;

    /**
     * 原文链接
     */
    @ApiModelProperty(value = "原文链接")
    private String fileUrl;


    @ApiModelProperty(value = "翻译标题")
    private String translateTitle;

    @ApiModelProperty(value = "翻译的摘要")
    private String translateAbstract;

    @ApiModelProperty(value = "AI总结")
    private String aiSummary;

    @ApiModelProperty(value = "AI总结状态")
    private Boolean aiSummaryStatus;


    public void setId(Long id) {
        this.id = id.toString();
    }
}
