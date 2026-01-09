package com.caring.sass.ai.dto.article;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
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
@ApiModel(value = "ArticleConvergenceMediaPageDTO", description = "知识库融媒体")
public class ArticleConvergenceMediaPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 博主ID
     */
    @ApiModelProperty(value = "博主ID")
    private Long userId;

    /**
     * 文件标题
     */
    @ApiModelProperty(value = "文件标题")
    @Length(max = 200, message = "文件标题长度不能超过200")
    private String fileTitle;


}
