package com.caring.sass.ai.dto.humanVideo;

import java.time.LocalDateTime;
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
 * 用户提交的录音素材
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BusinessUserAudioTemplatePageDTO", description = "用户提交的录音素材")
public class BusinessUserAudioTemplatePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件的url
     */
    @ApiModelProperty(value = "文件的url")
    @Length(max = 255, message = "文件的url长度不能超过255")
    private String fileUrl;
    /**
     * 任务状态(等待克隆， 克隆完成， 克隆失败)
     */
    @ApiModelProperty(value = "任务状态(等待克隆， 克隆完成， 克隆失败)")
    @Length(max = 255, message = "任务状态(等待克隆， 克隆完成， 克隆失败)长度不能超过255")
    private String taskStatus;
    /**
     * 克隆信息
     */
    @ApiModelProperty(value = "克隆信息")
    private String taskMessage;
    /**
     * 克隆使用的音色
     */
    @ApiModelProperty(value = "克隆使用的音色")
    @Length(max = 255, message = "克隆使用的音色长度不能超过255")
    private String timbreId;
    /**
     * 制作数字人视频任务ID
     */
    @ApiModelProperty(value = "制作数字人视频任务ID")
    private Long videoTaskId;

}
