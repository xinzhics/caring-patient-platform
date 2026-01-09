package com.caring.sass.ai.dto.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 声音管理
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
@ApiModel(value = "ArticleUserVoiceSaveDTO", description = "声音管理")
public class ArticleUserVoiceSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 音频链接
     */
    @ApiModelProperty(value = "音频链接")
    @Length(max = 500, message = "音频链接长度不能超过500")
    private String voiceUrl;
    /**
     * 1默认，其他否
     */
    @ApiModelProperty(value = "1默认，其他否")
    private Boolean defaultVoice;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;
    /**
     * 海螺平台文件id
     */
    @ApiModelProperty(value = "海螺平台文件id")
    private Long fileId;
    /**
     * 克隆试听音频链接
     */
    @ApiModelProperty(value = "克隆试听音频链接")
    @Length(max = 500, message = "克隆试听音频链接长度不能超过500")
    private String demoAudio;
    /**
     * 海螺是否克隆：1已克隆 0未克隆
     */
    @ApiModelProperty(value = "海螺是否克隆：1已克隆 0未克隆")
    private Boolean hasClone;

    @ApiModelProperty(value = "音频名称")
    private String name;

    /**
     * 海螺声音克隆id
     */
    @ApiModelProperty(value = "海螺声音克隆id")
    private String voiceId;

    /**
     * 海螺账户
     */
    @ApiModelProperty(value = "海螺账户")
    private String account;
}
