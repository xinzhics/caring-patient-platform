package com.caring.sass.ai.dto.article;

import com.baomidou.mybatisplus.annotation.TableField;
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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 视频底板
 * </p>
 *
 * @author leizhi
 * @since 2025-02-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleUserVideoTemplatePageDTO", description = "视频底板")
public class ArticleUserVideoTemplatePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull
    private Long userId;


    @ApiModelProperty(value = "类型1 图片, 2视频")
    @TableField(value = "type_", condition = EQUAL)
    private Integer type;

    /**
     * 底板名称
     */
    @ApiModelProperty(value = "底板名称")
    @Length(max = 255, message = "底板名称长度不能超过255")
    private String videoName;

    /**
     * 文件原始名称
     */
    @ApiModelProperty(value = "文件原始名称")
    @Length(max = 255, message = "文件原始名称长度不能超过255")
    private String fileName;


    @ApiModelProperty(value = "底板名称")
    private Boolean deleteStatus = false;

}
