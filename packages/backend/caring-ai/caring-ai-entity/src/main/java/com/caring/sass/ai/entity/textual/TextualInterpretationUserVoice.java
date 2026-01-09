package com.caring.sass.ai.entity.textual;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * 文献解读用户声音
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@Deprecated
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_textual_interpretation_user_voice")
@ApiModel(value = "TextualInterpretationUserVoice", description = "文献解读用户声音")
@AllArgsConstructor
public class TextualInterpretationUserVoice extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 音频链接
     */
    @ApiModelProperty(value = "音频链接")
    @Length(max = 500, message = "音频链接长度不能超过500")
    @TableField(value = "voice_url", condition = LIKE)
    @Excel(name = "音频链接")
    private String voiceUrl;

    /**
     * {"是_true", "否_false", "_null"}
     */
    @ApiModelProperty(value = "{是: 1, 否: 0, _null}")
    @TableField("default_voice")
    @Excel(name = "{是: 1, 否: 0, _null}")
    private Boolean defaultVoice;

    /**
     * 海螺平台文件id
     */
    @ApiModelProperty(value = "海螺平台文件id")
    @TableField("h_file_id")
    @Excel(name = "海螺平台文件id")
    private Long hFileId;

    /**
     * 海螺声音克隆id
     */
    @ApiModelProperty(value = "海螺声音克隆id")
    @Length(max = 255, message = "海螺声音克隆id长度不能超过255")
    @TableField(value = "h_voice_id", condition = LIKE)
    @Excel(name = "海螺声音克隆id")
    private String voiceId;

    /**
     * 克隆试听音频链接
     */
    @ApiModelProperty(value = "克隆试听音频链接")
    @Length(max = 500, message = "克隆试听音频链接长度不能超过500")
    @TableField(value = "h_demo_audio", condition = LIKE)
    @Excel(name = "克隆试听音频链接")
    private String demoAudio;

    /**
     * 海螺是否克隆：1已克隆 0未克隆
     */
    @ApiModelProperty(value = "海螺是否克隆：1已克隆 0未克隆")
    @TableField("h_has_clone")
    @Excel(name = "海螺是否克隆：1已克隆 0未克隆")
    private Boolean hasClone;

    /**
     * 音频名称
     */
    @ApiModelProperty(value = "音频名称")
    @Length(max = 255, message = "音频名称长度不能超过255")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "音频名称")
    private String name;

    /**
     * 失败原因
     */
    @ApiModelProperty(value = "失败原因")
    @Length(max = 255, message = "失败原因长度不能超过255")
    @TableField(value = "fail_reason", condition = LIKE)
    @Excel(name = "失败原因")
    private String failReason;

    /**
     * 删除状态
     */
    @ApiModelProperty(value = "删除状态")
    @TableField("delete_status")
    @Excel(name = "删除状态")
    private Boolean deleteStatus;

    /**
     * 使用次数
     */
    @ApiModelProperty(value = "使用次数")
    @TableField("use_count")
    @Excel(name = "使用次数")
    private Integer useCount;

    /**
     * 海螺账户
     */
    @ApiModelProperty(value = "海螺账户")
    @Length(max = 255, message = "海螺账户长度不能超过255")
    @TableField(value = "h_account", condition = LIKE)
    @Excel(name = "海螺账户")
    private String hAccount;




}
