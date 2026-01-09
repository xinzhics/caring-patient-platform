package com.caring.sass.ai.dto.podcast;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.ai.entity.podcast.DoubaoVoiceType;
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
 * 播客声音设置
 * </p>
 *
 * @author 杨帅
 * @since 2024-11-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PodcastSoundSetUpdateDTO", description = "播客声音设置")
public class PodcastSoundSetUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 角色名字
     */
    @ApiModelProperty(value = "角色名字")
    @Length(max = 20, message = "角色名字长度不能超过20")
    @NotNull
    @NotEmpty
    private String roleName;
    /**
     * 角色头衔
     */
    @ApiModelProperty(value = "角色头衔")
    @Length(max = 10, message = "角色头衔长度不能超过10")
    @NotNull
    @NotEmpty
    private String roleTitle;
    /**
     * 角色性别
     */
    @ApiModelProperty(value = "角色性别")
    @Length(max = 10, message = "角色性别长度不能超过10")
    @NotNull
    @NotEmpty
    private String roleGender;
    /**
     * 角色声音
     */
    @ApiModelProperty(value = "角色声音")
    @Length(max = 100, message = "角色声音长度不能超过100")
    @NotNull
    @NotEmpty
    private String roleSoundSet;

}
