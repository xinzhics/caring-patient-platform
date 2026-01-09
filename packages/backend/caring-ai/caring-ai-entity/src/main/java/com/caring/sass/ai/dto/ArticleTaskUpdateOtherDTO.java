package com.caring.sass.ai.dto;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
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
@ApiModel(value = "ArticleTaskUpdateOtherDTO", description = "Ai创作任务")
public class ArticleTaskUpdateOtherDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;
    /**
     * 文章字数
     */
    @ApiModelProperty(value = "文章字数")
    private Integer articleWordCount;
    /**
     * 自动配图 0 关闭 1 开启
     */
    @ApiModelProperty(value = "自动配图 0 关闭 1 开启")
    private Integer automaticPictureMatching;


}
