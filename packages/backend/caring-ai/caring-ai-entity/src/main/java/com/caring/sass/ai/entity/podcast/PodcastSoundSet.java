package com.caring.sass.ai.entity.podcast;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 播客声音设置
 * </p>
 *
 * @author 杨帅
 * @since 2024-11-12
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_podcast_sound_set")
@ApiModel(value = "PodcastSoundSet", description = "播客声音设置")
@AllArgsConstructor
public class PodcastSoundSet extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 播客ID
     */
    @ApiModelProperty(value = "播客ID")
    @NotNull(message = "播客ID不能为空")
    @TableField("podcast_id")
    @Excel(name = "播客ID")
    private Long podcastId;

    /**
     * 角色名字
     */
    @ApiModelProperty(value = "角色名字")
    @Length(max = 20, message = "角色名字长度不能超过20")
    @TableField(value = "role_name", condition = LIKE)
    @Excel(name = "角色名字")
    private String roleName;

    /**
     * 角色头衔
     */
    @ApiModelProperty(value = "角色头衔")
    @Length(max = 10, message = "角色头衔长度不能超过10")
    @TableField(value = "role_title", condition = LIKE)
    @Excel(name = "角色头衔")
    private String roleTitle;

    /**
     * 角色性别
     */
    @ApiModelProperty(value = "角色性别")
    @Length(max = 10, message = "角色性别长度不能超过10")
    @TableField(value = "role_gender", condition = LIKE)
    @Excel(name = "角色性别")
    private String roleGender;

    /**
     * 角色声音
     */
    @ApiModelProperty(value = "角色声音")
    @Length(max = 100, message = "角色声音长度不能超过100")
    @TableField(value = "role_sound_set", condition = LIKE)
    @Excel(name = "角色声音")
    private String roleSoundSet;

    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "所属用户ID")
    @NotNull(message = "所属用户ID不能为空")
    @TableField("user_id")
    @Excel(name = "所属用户ID")
    private Long userId;




}
