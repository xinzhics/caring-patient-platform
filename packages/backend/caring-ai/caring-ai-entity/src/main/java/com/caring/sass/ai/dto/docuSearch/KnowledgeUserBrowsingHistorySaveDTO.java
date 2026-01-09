package com.caring.sass.ai.dto.docuSearch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

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
 * 知识库证据浏览记录
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
@ApiModel(value = "KnowledgeUserBrowsingHistorySaveDTO", description = "知识库证据浏览记录")
public class KnowledgeUserBrowsingHistorySaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;
    /**
     * 问题证据id
     */
    @ApiModelProperty(value = "问题证据id")
    @NotNull(message = "问题证据id不能为空")
    private Long problemEvidenceId;
    /**
     * 浏览时间
     */
    @ApiModelProperty(value = "浏览时间")
    @NotNull(message = "浏览时间不能为空")
    private Long viewTime;

}
