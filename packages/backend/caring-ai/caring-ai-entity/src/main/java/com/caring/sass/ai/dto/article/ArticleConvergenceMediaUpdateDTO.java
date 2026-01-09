package com.caring.sass.ai.dto.article;

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
 * 知识库融媒体
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleConvergenceMediaUpdateDTO", description = "知识库融媒体")
public class ArticleConvergenceMediaUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;


    /**
     * 文件标题
     */
    @ApiModelProperty(value = "文件标题")
    @Length(max = 200, message = "文件标题长度不能超过200")
    private String fileTitle;
    /**
     * 链接路径
     */
    @ApiModelProperty(value = "链接路径")
    private String fileUrl;
}
