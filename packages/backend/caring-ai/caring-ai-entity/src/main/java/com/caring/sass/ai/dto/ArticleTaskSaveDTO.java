package com.caring.sass.ai.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
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
@ApiModel(value = "ArticleTaskSaveDTO", description = "Ai创作任务")
public class ArticleTaskSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会话标识")
    private String uid;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    @Length(max = 500, message = "关键词长度不能超过500")
    private String keyWords;
    /**
     * 受众
     */
    @ApiModelProperty(value = "受众")
    @Length(max = 500, message = "受众长度不能超过500")
    private String audience;
    /**
     * 写作风格
     */
    @ApiModelProperty(value = "写作风格")
    @Length(max = 100, message = "写作风格长度不能超过100")
    private String writingStyle;
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

    @ApiModelProperty(value = "使用的版本 v1  v2")
    private String version;


}
