package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * Ai创作用户形象
 * </p>
 *
 * @author xinz
 * @since 2024-08-01
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_article_user_avatar", autoResultMap = true)
@ApiModel(value = "ArticleUserAvatar", description = "Ai创作用户形象")
@AllArgsConstructor
public class ArticleUserAvatar extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 图片链接
     */
    @ApiModelProperty(value = "图片链接")
    @Length(max = 500, message = "用户图片链接长度不能超过500")
    @TableField(value = "avatar_url")
    @Excel(name = "用户图片链接")
    private String avatarUrl;


    @ApiModelProperty(value = "是否默认形象")
    @TableField(value = "default_avatar", condition = EQUAL)
    private Boolean defaultAvatar;

    @ApiModelProperty(value = "用户id")
    @TableField(value = "user_id", condition = EQUAL)
    private Long userId;


}
