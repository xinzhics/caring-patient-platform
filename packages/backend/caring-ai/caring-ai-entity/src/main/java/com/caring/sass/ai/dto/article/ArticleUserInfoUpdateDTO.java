package com.caring.sass.ai.dto.article;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.constant.GenderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * Ai创作用户
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
@ApiModel(value = "ArticleUserInfoUpdateDTO", description = "修改AI创作用户信息")
public class ArticleUserInfoUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户头像")
    @TableField(value = "user_avatar", condition = LIKE)
    private String userAvatar;

    @ApiModelProperty(value = "用户昵称")
    @TableField(value = "user_name", condition = LIKE)
    private String userName;

    @ApiModelProperty(value = "用户性别")
    @TableField(value = "user_gender", condition = EQUAL)
    private GenderType userGender;


    @ApiModelProperty(value = "个人简介")
    @TableField(value = "personal_profile", condition = EQUAL)
    private String personalProfile;





}
