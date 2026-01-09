package com.caring.sass.ai.dto.article;

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
 * 形象管理
 * </p>
 *
 * @author leizhi
 * @since 2025-02-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleUserAvatarSaveDTO", description = "形象管理")
public class ArticleUserAvatarSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片链接
     */
    @ApiModelProperty(value = "图片链接")
    @Length(max = 500, message = "图片链接长度不能超过500")
    private String avatarUrl;
    /**
     * 1默认，其他否
     */
    @ApiModelProperty(value = "1默认，其他否")
    private Boolean defaultAvatar;

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id")
    private Long userId;

}
