package com.caring.sass.ai.dto.textual;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import com.caring.sass.base.entity.SuperEntity;
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
 * 文献解读用户表
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TextualInterpretationUserUpdateDTO", description = "文献解读用户表")
public class TextualInterpretationUserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 100, message = "用户手机号长度不能超过100")
    private String userMobile;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @Length(max = 400, message = "密码长度不能超过400")
    private String password;
    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    @Length(max = 255, message = "用户头像长度不能超过255")
    private String userAvatar;
    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @Length(max = 255, message = "用户昵称长度不能超过255")
    private String userName;
    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别")
    @Length(max = 255, message = "用户性别长度不能超过255")
    private String userGender;
    /**
     * 个人简介
     */
    @ApiModelProperty(value = "个人简介")
    @Length(max = 500, message = "个人简介长度不能超过500")
    private String personalProfile;
    /**
     * 会员等级
     */
    @ApiModelProperty(value = "会员等级")
    @Length(max = 255, message = "会员等级长度不能超过255")
    private String membershipLevel;
    /**
     * 会员到期时间
     */
    @ApiModelProperty(value = "会员到期时间")
    private LocalDateTime membershipExpiration;
    /**
     * 会员能量豆
     */
    @ApiModelProperty(value = "会员能量豆")
    private Long energyBeans;
    /**
     * 注册用户赠送的20能量豆
     */
    @ApiModelProperty(value = "注册用户赠送的20能量豆")
    private Long freeEnergyBeans;
    /**
     * 是否完成首次创作
     */
    @ApiModelProperty(value = "是否完成首次创作")
    private Integer firstCreation;
    /**
     * 第7天是否活跃
     */
    @ApiModelProperty(value = "第7天是否活跃")
    private Integer day7Active;
    /**
     * 第30天是否活跃
     */
    @ApiModelProperty(value = "第30天是否活跃")
    private Integer day30Active;
    /**
     * 完成首次创作的日期
     */
    @ApiModelProperty(value = "完成首次创作的日期")
    private LocalDateTime firstCreationDate;
}
