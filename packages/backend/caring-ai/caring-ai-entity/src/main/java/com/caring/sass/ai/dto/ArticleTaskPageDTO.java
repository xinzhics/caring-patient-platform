package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * Ai创作任务
 * </p>
 *
 * @author 杨帅
 * @since 2024-08-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleTaskPageDTO", description = "Ai创作任务")
public class ArticleTaskPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    @Length(max = 500, message = "关键词长度不能超过500")
    private String keyWords;

    /**
     * 选择的标题
     */
    @ApiModelProperty(value = "标题")
    private String title;


    @NotNull
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "任务状态 0 创作中 1 创作完成 2 存为草稿")
    private Integer taskStatus = 2;

    @ApiModelProperty(value = "展示在ai医生数字分身平台")
    private Boolean showAIKnowledge;

}
