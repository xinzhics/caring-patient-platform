package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.ai.dto.article.VoiceCloneStatus;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_article_user_voice", autoResultMap = true)
@ApiModel(value = "ArticleUserVoice", description = "声音管理")
@AllArgsConstructor
public class ArticleUserVoice extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户手机号")
    @TableField(value = "user_mobile", condition = EQUAL, typeHandler = EncryptedStringTypeHandler.class)
    private String userMobile;

    /**
     * 音频链接
     */
    @ApiModelProperty(value = "音频链接")
    @Length(max = 500, message = "音频链接长度不能超过500")
    @TableField(value = "voice_url", condition = LIKE)
    @Excel(name = "音频链接")
    private String voiceUrl;

    /**
     * 1默认，其他否
     */
    @ApiModelProperty(value = "1默认，其他否")
    @TableField("default_voice")
    @Excel(name = "1默认，其他否", replace = {"是_true", "否_false", "_null"})
    private Boolean defaultVoice;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 海螺平台文件id
     */
    @ApiModelProperty(value = "海螺平台文件id")
    @TableField("h_file_id")
    @Excel(name = "海螺平台文件id")
    private Long fileId;

    /**
     * 海螺声音克隆id
     */
    @ApiModelProperty(value = "海螺声音克隆id")
    @TableField("h_voice_id")
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
    @Excel(name = "海螺是否克隆：1已克隆 0未克隆", replace = {"是_true", "否_false", "_null"})
    private Boolean hasClone;

    @ApiModelProperty(value = "克隆状态")
    @TableField(value = "clone_status")
    private VoiceCloneStatus cloneStatus;

    @TableField("video_template_id")
    @Excel(name = "视频模板id")
    private Long videoTemplateId;

    /**
     * 音频名称
     */
    @ApiModelProperty(value = "音频名称")
    @Length(max = 255, message = "音频名称过长")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "音频名称")
    private String name;

    @ApiModelProperty(value = "使用次数")
    @TableField(value = "use_count")
    private Integer useCount;

    @ApiModelProperty(value = "删除状态")
    @TableField(value = "delete_status")
    private Boolean deleteStatus;

    @ApiModelProperty(value = "海螺音色过期 0 未过期， 1 已过期")
    @TableField(value = "mini_expired")
    private Integer miniExpired;

    /**
     * 失败原因
     */
    @ApiModelProperty(value = "失败原因")
    @Length(max = 255, message = "失败原因")
    @Excel(name = "失败原因")
    private String failReason;

    /**
     * 海螺账户
     */
    @ApiModelProperty(value = "海螺账户")
    @Length(max = 500, message = "海螺账户")
    @TableField(value = "h_account")
    @Excel(name = "海螺账户")
    private String account;

    @ApiModelProperty(value = "失败退款")
    @TableField(value = "fail_refund")
    private Boolean failRefund;

    @TableField(value = "textual_")
    @Excel(name = "是否为文献解读")
    private Boolean textual;

    @Builder
    public ArticleUserVoice(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime,
                            String voiceUrl, Boolean defaultVoice, Long userId, Long fileId,
                            String demoAudio, Boolean hasClone, String failReason, String account) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.voiceUrl = voiceUrl;
        this.defaultVoice = defaultVoice;
        this.userId = userId;
        this.fileId = fileId;
        this.demoAudio = demoAudio;
        this.hasClone = hasClone;
        this.failReason = failReason;
        this.account = account;
    }

}
