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
@ApiModel(value = "KnowledgeUserQuestionPageDTO", description = "知识库用户问题")
public class KnowledgeUserQuestionPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 问题描述
     */
    @ApiModelProperty(value = "问题描述")
    @Length(max = 500, message = "问题描述长度不能超过500")
    private String questionDesc;

}
