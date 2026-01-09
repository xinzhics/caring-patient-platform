package com.caring.sass.ai.dto.docuSearch;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.ai.entity.docuSearch.QuestionAnalyzeStatus;
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
 * 知识库用户问题
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
@ApiModel(value = "KnowledgeUserQuestionSaveModel", description = "知识库用户问题")
public class KnowledgeUserQuestionSaveModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 问题描述
     */
    @ApiModelProperty(value = "问题描述")
    private String questionDesc;


    @ApiModelProperty(value = "UId")
    private String uid;

    /**
     * 问题分析状态
     */
    @ApiModelProperty(value = "问题分析状态  理解问题， 分析问题， 查找证据  分析完毕")
    private QuestionAnalyzeStatus questionAnalyzeStatus;

    /**
     * 问题证据数量
     */
    @ApiModelProperty(value = "问题证据数量")
    private Integer questionQuantityEvidence;

    /**
     * 问题证据数量
     */
    @ApiModelProperty(value = "会话")
    private String conversation;

    @ApiModelProperty(value = "证据列表")
    private String evidenceIds;

    /**
     * 是否本组最新问题
     */
    @ApiModelProperty(value = "是否本组最新问题")
    private Integer isNewQuestion;
}
