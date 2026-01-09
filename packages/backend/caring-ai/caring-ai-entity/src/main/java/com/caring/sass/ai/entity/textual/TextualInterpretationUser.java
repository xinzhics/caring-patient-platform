package com.caring.sass.ai.entity.textual;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.ai.entity.article.ArticleMembershipLevel;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.constant.GenderType;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 文献解读用户表
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_textual_interpretation_user", autoResultMap = true)
@ApiModel(value = "TextualInterpretationUser", description = "文献解读用户表")
@AllArgsConstructor
public class TextualInterpretationUser extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 100, message = "用户手机号长度不能超过100")
    @TableField(value = "user_mobile", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "用户手机号")
    private String userMobile;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @Length(max = 400, message = "密码长度不能超过400")
    @TableField(value = "password", condition = LIKE)
    @Excel(name = "密码")
    private String password;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    @Length(max = 255, message = "用户头像长度不能超过255")
    @TableField(value = "user_avatar", condition = LIKE)
    @Excel(name = "用户头像")
    private String userAvatar;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @Length(max = 255, message = "用户昵称长度不能超过255")
    @TableField(value = "user_name", condition = LIKE)
    @Excel(name = "用户昵称")
    private String userName;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别")
    @Length(max = 255, message = "用户性别长度不能超过255")
    @TableField(value = "user_gender", condition = LIKE)
    @Excel(name = "用户性别")
    private GenderType userGender;

    /**
     * 个人简介
     */
    @ApiModelProperty(value = "个人简介")
    @Length(max = 500, message = "个人简介长度不能超过500")
    @TableField(value = "personal_profile", condition = LIKE)
    @Excel(name = "个人简介")
    private String personalProfile;

    /**
     * 会员等级
     */
    @ApiModelProperty(value = "会员等级")
    @Length(max = 255, message = "会员等级长度不能超过255")
    @TableField(value = "membership_level", condition = LIKE)
    @Excel(name = "会员等级")
    private ArticleMembershipLevel membershipLevel;

    /**
     * 会员到期时间
     */
    @ApiModelProperty(value = "会员到期时间")
    @TableField("membership_expiration")
    @Excel(name = "会员到期时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime membershipExpiration;

    /**
     * 会员能量豆
     */
    @ApiModelProperty(value = "会员能量豆")
    @TableField("energy_beans")
    @Excel(name = "会员能量豆")
    private Long energyBeans;

    /**
     * 注册用户赠送的20能量豆
     */
    @ApiModelProperty(value = "注册用户赠送的20能量豆")
    @TableField("free_energy_beans")
    @Excel(name = "注册用户赠送的20能量豆")
    private Long freeEnergyBeans;

    /**
     * 是否完成首次创作
     */
    @ApiModelProperty(value = "是否完成首次创作")
    @TableField("first_creation")
    @Excel(name = "是否完成首次创作")
    private Integer firstCreation;

    /**
     * 第7天是否活跃
     */
    @ApiModelProperty(value = "第7天是否活跃")
    @TableField("day_7_active")
    @Excel(name = "第7天是否活跃")
    private Integer day7Active;

    /**
     * 第30天是否活跃
     */
    @ApiModelProperty(value = "第30天是否活跃")
    @TableField("day_30_active")
    @Excel(name = "第30天是否活跃")
    private Integer day30Active;

    /**
     * 完成首次创作的日期
     */
    @ApiModelProperty(value = "完成首次创作的日期")
    @TableField("first_creation_date")
    @Excel(name = "完成首次创作的日期", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime firstCreationDate;

    /**
     * 是否是知识库博主
     */
    @ApiModelProperty(value = "是否是知识库博主")
    @TableField(exist = false)
    private boolean knowledgeChiefPhysician;


    @Builder
    public TextualInterpretationUser(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String userMobile, String password, String userAvatar, String userName, GenderType userGender,
                    String personalProfile, ArticleMembershipLevel membershipLevel, LocalDateTime membershipExpiration, Long energyBeans, Long freeEnergyBeans, Integer firstCreation,
                    Integer day7Active, Integer day30Active, LocalDateTime firstCreationDate) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userMobile = userMobile;
        this.password = password;
        this.userAvatar = userAvatar;
        this.userName = userName;
        this.userGender = userGender;
        this.personalProfile = personalProfile;
        this.membershipLevel = membershipLevel;
        this.membershipExpiration = membershipExpiration;
        this.energyBeans = energyBeans;
        this.freeEnergyBeans = freeEnergyBeans;
        this.firstCreation = firstCreation;
        this.day7Active = day7Active;
        this.day30Active = day30Active;
        this.firstCreationDate = firstCreationDate;
    }

}
