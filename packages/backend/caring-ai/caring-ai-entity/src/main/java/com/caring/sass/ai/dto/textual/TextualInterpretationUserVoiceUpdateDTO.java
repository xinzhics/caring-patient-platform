package com.caring.sass.ai.dto.textual;

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
 * 文献解读用户声音
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
@ApiModel(value = "TextualInterpretationUserVoiceUpdateDTO", description = "文献解读用户声音")
public class TextualInterpretationUserVoiceUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;
    /**
     * 音频链接
     */
    @ApiModelProperty(value = "音频链接")
    @Length(max = 500, message = "音频链接长度不能超过500")
    private String voiceUrl;
    /**
     * {"是_true", "否_false", "_null"}
     */
    @ApiModelProperty(value = "{是: 1, 否: 0, _null}")
    private Integer defaultVoice;
    /**
     * 海螺平台文件id
     */
    @ApiModelProperty(value = "海螺平台文件id")
    private Long hFileId;
    /**
     * 海螺声音克隆id
     */
    @ApiModelProperty(value = "海螺声音克隆id")
    @Length(max = 255, message = "海螺声音克隆id长度不能超过255")
    private String hVoiceId;
    /**
     * 克隆试听音频链接
     */
    @ApiModelProperty(value = "克隆试听音频链接")
    @Length(max = 500, message = "克隆试听音频链接长度不能超过500")
    private String hDemoAudio;
    /**
     * 海螺是否克隆：1已克隆 0未克隆
     */
    @ApiModelProperty(value = "海螺是否克隆：1已克隆 0未克隆")
    private Integer hHasClone;
    /**
     * 音频名称
     */
    @ApiModelProperty(value = "音频名称")
    @Length(max = 255, message = "音频名称长度不能超过255")
    private String name;
    /**
     * 失败原因
     */
    @ApiModelProperty(value = "失败原因")
    @Length(max = 255, message = "失败原因长度不能超过255")
    private String failReason;
    /**
     * 删除状态
     */
    @ApiModelProperty(value = "删除状态")
    private Integer deleteStatus;
    /**
     * 使用次数
     */
    @ApiModelProperty(value = "使用次数")
    private Integer useCount;
    /**
     * 海螺账户
     */
    @ApiModelProperty(value = "海螺账户")
    @Length(max = 255, message = "海螺账户长度不能超过255")
    private String hAccount;
}
