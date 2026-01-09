package com.caring.sass.tenant.dto;

import com.caring.sass.common.enums.BatchBuildTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BatchBuildTaskPage
 * @Description
 * @Author yangShuai
 * @Date 2021/10/26 16:46
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "BatchBuildTaskPage", description = "分页")
public class BatchBuildTaskPage {

    @ApiModelProperty(value = "打包状态")
    private BatchBuildTask taskStatus;


    @ApiModelProperty(value = "批量任务")
    private Long batchBuildApkTaskId;



}
