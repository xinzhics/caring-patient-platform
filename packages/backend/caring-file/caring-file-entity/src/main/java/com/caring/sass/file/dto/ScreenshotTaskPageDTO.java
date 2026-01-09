package com.caring.sass.file.dto;

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
 * 视频截图任务
 * </p>
 *
 * @author 杨帅
 * @since 2023-04-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ScreenshotTaskPageDTO", description = "视频截图任务")
public class ScreenshotTaskPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 视频文件名称
     */
    @ApiModelProperty(value = "视频文件名称")
    @Length(max = 255, message = "视频文件名称长度不能超过255")
    private String fileName;
    /**
     * 华为截图任务ID
     */
    @ApiModelProperty(value = "华为截图任务ID")
    @Length(max = 255, message = "华为截图任务ID长度不能超过255")
    private String taskId;
    /**
     * 华为截图任务状态
     */
    @ApiModelProperty(value = "华为截图任务状态")
    @Length(max = 255, message = "华为截图任务状态长度不能超过255")
    private String status;
    /**
     * 输出文件信息
     */
    @ApiModelProperty(value = "输出文件信息")
    @Length(max = 65535, message = "输出文件信息长度不能超过65,535")
    private String output;
    /**
     * 截图文件名称
     */
    @ApiModelProperty(value = "截图文件名称")
    @Length(max = 300, message = "截图文件名称长度不能超过300")
    private String outputFileName;
    /**
     * 指定的截图时间点
     */
    @ApiModelProperty(value = "指定的截图时间点")
    @Length(max = 300, message = "指定的截图时间点长度不能超过300")
    private String thumbnailTime;
    /**
     * 截图任务描述，当截图出现异常时，此字段为异常的原因
     */
    @ApiModelProperty(value = "截图任务描述，当截图出现异常时，此字段为异常的原因")
    @Length(max = 65535, message = "截图任务描述，当截图出现异常时，此字段为异常的原因长度不能超过65,535")
    private String description;

}
