package com.caring.sass.tenant.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName BatchBuildApkDto
 * @Description
 * @Author yangShuai
 * @Date 2021/10/26 14:06
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "BatchBuildApkDto", description = "提交打包")
public class BatchBuildApkDto {

    @ApiModelProperty("任务名称")
    private String taskName;

    /**
     * 是否全部打包
     */
    @NotNull
    @ApiModelProperty("是否全部打包")
    private Boolean allBuild;

    @ApiModelProperty("选中的打包项目")
    private List<Long> tenantIds;

}
