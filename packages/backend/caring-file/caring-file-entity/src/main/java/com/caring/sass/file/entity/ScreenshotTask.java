package com.caring.sass.file.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
 * 视频截图任务
 * </p>
 *
 * @author 杨帅
 * @since 2023-04-06
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("f_file_screenshot_task")
@ApiModel(value = "ScreenshotTask", description = "视频截图任务")
@AllArgsConstructor
public class ScreenshotTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 视频文件名称
     */
    @ApiModelProperty(value = "视频文件名称")
    @Length(max = 255, message = "视频文件名称长度不能超过255")
    @TableField(value = "file_name", condition = LIKE)
    @Excel(name = "视频文件名称")
    private String fileName;

    /**
     * 华为截图任务ID
     */
    @ApiModelProperty(value = "华为截图任务ID")
    @Length(max = 255, message = "华为截图任务ID长度不能超过255")
    @TableField(value = "task_id", condition = LIKE)
    @Excel(name = "华为截图任务ID")
    private String taskId;

    /**
     * 华为截图任务状态
     */
    @ApiModelProperty(value = "华为截图任务状态")
    @Length(max = 255, message = "华为截图任务状态长度不能超过255")
    @TableField(value = "status", condition = LIKE)
    @Excel(name = "华为截图任务状态")
    private String status;

    /**
     * 输出文件信息
     */
    @ApiModelProperty(value = "输出文件信息")
    @Length(max = 65535, message = "输出文件信息长度不能超过65535")
    @TableField("output")
    @Excel(name = "输出文件信息")
    private String output;

    /**
     * 截图文件名称
     */
    @ApiModelProperty(value = "截图文件名称")
    @Length(max = 300, message = "截图文件名称长度不能超过300")
    @TableField(value = "output_file_name", condition = LIKE)
    @Excel(name = "截图文件名称")
    private String outputFileName;

    /**
     * 指定的截图时间点
     */
    @ApiModelProperty(value = "指定的截图时间点")
    @Length(max = 300, message = "指定的截图时间点长度不能超过300")
    @TableField(value = "thumbnail_time", condition = LIKE)
    @Excel(name = "指定的截图时间点")
    private String thumbnailTime;

    /**
     * 截图任务描述，当截图出现异常时，此字段为异常的原因
     */
    @ApiModelProperty(value = "截图任务描述，当截图出现异常时，此字段为异常的原因")
    @Length(max = 65535, message = "截图任务描述，当截图出现异常时，此字段为异常的原因长度不能超过65535")
    @TableField("description")
    @Excel(name = "截图任务描述，当截图出现异常时，此字段为异常的原因")
    private String description;

    @ApiModelProperty(value = "封面地址")
    @TableField(exist = false)
    private String coverUrl;

    @Builder
    public ScreenshotTask(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String fileName, String taskId, String status, String output, String outputFileName, 
                    String thumbnailTime, String description) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.fileName = fileName;
        this.taskId = taskId;
        this.status = status;
        this.output = output;
        this.outputFileName = outputFileName;
        this.thumbnailTime = thumbnailTime;
        this.description = description;
    }

}
