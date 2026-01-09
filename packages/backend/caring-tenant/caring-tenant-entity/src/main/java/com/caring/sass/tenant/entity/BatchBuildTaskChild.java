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
 * @ClassName BatchBuildTaskChild
 * @Description
 * @Author yangShuai
 * @Date 2021/10/26 10:35
 * @Version 1.0
 */

@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_tenant_batch_build_child_task")
@ApiModel(value = "BatchBuildTaskChild", description = "批量打包的子任务")
@AllArgsConstructor
public class BatchBuildTaskChild extends Entity<Long> {


    @ApiModelProperty(value = "批量任务")
    @TableField(value = "batch_build_apk_task_id")
    private Long batchBuildApkTaskId;

    @ApiModelProperty(value = "app原先版本")
    @TableField(value = "app_version_name_last")
    private String appVersionNameLast;

    @ApiModelProperty(value = "app升级后版本")
    @TableField(value = "app_version_name")
    private String appVersionName;

    @ApiModelProperty(value = "结束时间")
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "开始时间")
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "状态说明")
    @TableField(value = "message")
    private String message;

    @ApiModelProperty(value = "项目code")
    @TableField(value = "code", condition = EQUAL)
    private String code;

    @ApiModelProperty(value = "任务状态")
    @TableField(value = "task_status", condition = EQUAL)
    private BatchBuildTask taskStatus;

    @ApiModelProperty(value = "项目名称")
    @TableField(exist = false)
    private String tenantName;

    @ApiModelProperty(value = "最后更新时间")
    @TableField(value = "last_update_time")
    private Long lastUpdateTime;
}
