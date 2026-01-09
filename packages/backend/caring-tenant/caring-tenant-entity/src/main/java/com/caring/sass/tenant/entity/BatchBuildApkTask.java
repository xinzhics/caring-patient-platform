package com.caring.sass.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.enums.BatchBuildTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName BatchBuildApkTask
 * @Description
 * @Author yangShuai
 * @Date 2021/10/26 10:24
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_tenant_batch_build_apktask")
@ApiModel(value = "BatchBuildApkTask", description = "批量打包apk任务")
@AllArgsConstructor
public class BatchBuildApkTask extends Entity<Long> {


    @ApiModelProperty(value = "任务名称")
    @TableField(value = "task_name")
    private String taskName;

    @ApiModelProperty(value = "结束时间")
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "开始时间")
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "总打包数量")
    @TableField(value = "all_task")
    private Integer allTask;

    @ApiModelProperty(value = "打包状态")
    @TableField(value = "task_status", condition = EQUAL)
    private BatchBuildTask taskStatus;

    @ApiModelProperty(value = "取消打包数量")
    @TableField(exist = false)
    private Integer stop;

    @ApiModelProperty(value = "异常打包数量")
    @TableField(exist = false)
    private Integer fail;

    @ApiModelProperty(value = "完成打包数量")
    @TableField(exist = false)
    private Integer finish;

    @ApiModelProperty(value = "最后更新时间")
    @TableField(value = "last_update_time")
    private Long lastUpdateTime;

}
