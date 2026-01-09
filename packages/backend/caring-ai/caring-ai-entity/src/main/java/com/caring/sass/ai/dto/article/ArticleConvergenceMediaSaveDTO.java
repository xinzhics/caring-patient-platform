package com.caring.sass.ai.dto.article;

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
@ApiModel(value = "ArticleConvergenceMediaSaveDTO", description = "知识库融媒体")
public class ArticleConvergenceMediaSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 博主ID
     */
    @ApiModelProperty(value = "博主ID")
    @NotNull
    private Long userId;
    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 100, message = "用户手机号长度不能超过100")
    private String userMobile;
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
