package com.caring.sass.ai.dto.docuSearch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
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
@ApiModel(value = "KnowledgeUserQuestionUpdateDTO", description = "知识库用户问题")
public class KnowledgeUserQuestionUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;
    /**
     * 问题描述
     */
    @ApiModelProperty(value = "问题描述")
    @Length(max = 500, message = "问题描述长度不能超过500")
    private String questionDesc;
    /**
     * 问题分析状态
     */
    @ApiModelProperty(value = "问题分析状态")
    @Length(max = 500, message = "问题分析状态长度不能超过500")
    private String questionAnalyzeStatus;
    /**
     * 问题证据数量
     */
    @ApiModelProperty(value = "问题证据数量")
    private Integer questionQuantityEvidence;
    /**
     * 问题证据数量
     */
    @ApiModelProperty(value = "问题证据数量")
    @Length(max = 50, message = "问题证据数量长度不能超过50")
    private String conversation;
    /**
     * 是否本组最新问题
     */
    @ApiModelProperty(value = "是否本组最新问题")
    private Integer isNewQuestion;
}
