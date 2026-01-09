package com.caring.sass.ai.dto;

import com.caring.sass.base.entity.SuperEntity;
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
 * Ai创作文章大纲
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
@ApiModel(value = "ArticleTitleUpdateDTO", description = "Ai创作文章大纲")
public class ArticleTitleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * AI创作任务
     */
    @ApiModelProperty(value = "AI创作任务")
    private Long taskId;
    /**
     * 文章大纲
     */
    @ApiModelProperty(value = "文章大纲")
    @Length(max = 500, message = "文章大纲长度不能超过500")
    private String articleTitle;
}
