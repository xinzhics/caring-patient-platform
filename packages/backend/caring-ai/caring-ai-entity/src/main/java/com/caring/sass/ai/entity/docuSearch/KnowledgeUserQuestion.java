package com.caring.sass.ai.entity.docuSearch;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 知识库用户问题
 * </p>
 *
 * @author 杨帅
 * @since 2024-10-17
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_user_question")
@ApiModel(value = "KnowledgeUserQuestion", description = "知识库用户问题")
@AllArgsConstructor
public class KnowledgeUserQuestion extends Entity<Long> {

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
     * 问题描述
     */
    @ApiModelProperty(value = "问题描述")
    @Length(max = 500, message = "问题描述长度不能超过500")
    @TableField(value = "question_desc", condition = LIKE)
    @Excel(name = "问题描述")
    private String questionDesc;


    @ApiModelProperty(value = "问题的keyword")
    @TableField(value = "question_keyword", condition = EQUAL)
    private String questionKeyword;



    @ApiModelProperty(value = "keyword的有效期")
    @TableField(value = "keyword_expire_time")
    private LocalDateTime keywordExpireTime;



    @ApiModelProperty(value = "UId")
    @TableField(value = "uid", condition = EQUAL)
    private String uid;

    /**
     * 问题分析状态
     */
    @ApiModelProperty(value = "问题分析状态  理解问题， 分析问题， 查找证据  分析完毕")
    @Length(max = 500, message = "问题分析状态长度不能超过500")
    @TableField(value = "question_analyze_status", condition = LIKE)
    @Excel(name = "问题分析状态")
    private QuestionAnalyzeStatus questionAnalyzeStatus;

    /**
     * 问题证据数量
     */
    @ApiModelProperty(value = "问题证据数量")
    @TableField("question_quantity_evidence")
    @Excel(name = "问题证据数量")
    private Integer questionQuantityEvidence;

    /**
     * 问题证据数量
     */
    @ApiModelProperty(value = "会话")
    @TableField(value = "conversation", condition = EQUAL)
    @Excel(name = "会话")
    private String conversation;

    @ApiModelProperty(value = "证据列表")
    @TableField(value = "evidence_ids", condition = EQUAL)
    private String evidenceIds;

    /**
     * 是否本组最新问题
     */
    @ApiModelProperty(value = "是否本组最新问题")
    @TableField("is_new_question")
    @Excel(name = "是否本组最新问题")
    private Integer isNewQuestion;

    @ApiModelProperty("开始搜索的时间")
    @TableField("start_search_time")
    private LocalDateTime startSearchTime;

    @ApiModelProperty("开始搜索的时间")
    @TableField("finish_search_time")
    private LocalDateTime finishSearchTime;

    @ApiModelProperty("问题的证据列表")
    @TableField(exist = false)
    private List<KnowledgeQuestionEvidence> questionEvidences;


}
