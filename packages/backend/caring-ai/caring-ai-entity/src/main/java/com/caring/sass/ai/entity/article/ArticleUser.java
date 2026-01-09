package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.constant.GenderType;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_article_user", autoResultMap = true)
@ApiModel(value = "ArticleUser", description = "Ai创作用户")
@AllArgsConstructor
public class ArticleUser extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 20, message = "用户手机号长度不能超过20")
    @TableField(value = "user_mobile", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "用户手机号")
    private String userMobile;


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


    @ApiModelProperty(value = "用户密码")
    @Length(max = 300, message = "用户密码长度不能超过300")
    @TableField(value = "password", condition = EQUAL)
    private String password;


    @ApiModelProperty(value = "用户类型 会员等级")
    @TableField(value = "membership_level", condition = EQUAL)
    private ArticleMembershipLevel membershipLevel;

    @ApiModelProperty(value = "会员到期时间")
    @TableField(value = "membership_expiration")
    private LocalDateTime membershipExpiration;

    @ApiModelProperty(value = "注册赠送能量豆")
    @TableField(value = "free_energy_beans")
    private Long freeEnergyBeans;

    @ApiModelProperty(value = "会员能量豆")
    @TableField(value = "energy_beans")
    private Long energyBeans;


    @ApiModelProperty(value = "是否查看引导")
    @TableField(value = "look_guide")
    private Boolean lookGuide;


    @ApiModelProperty(value = "是否知识库博主")
    @TableField(exist = false)
    private boolean knowledgeChiefPhysician;


    @ApiModelProperty(value = "是否完成首次创作")
    @Deprecated
    @TableField(value = "first_creation")
    private boolean firstCreation;

    @ApiModelProperty(value = "首次创作日期")
    @Deprecated
    @TableField(value = "first_creation_date")
    private LocalDate firstCreationDate;

    @ApiModelProperty(value = "7天活跃")
    @TableField(value = "day_7_active")
    private boolean day7Active;

    @ApiModelProperty(value = "30天活跃")
    @TableField(value = "day_30_active")
    private boolean day30Active;


}
