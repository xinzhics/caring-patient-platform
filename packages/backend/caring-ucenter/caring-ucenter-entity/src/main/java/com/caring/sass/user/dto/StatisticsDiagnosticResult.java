package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName StatisticsDiagnosticResult
 * @Description
 * @Author yangShuai
 * @Date 2022/6/1 14:35
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "StatisticsDiagnosticResult", description = "诊断类型统计")
public class StatisticsDiagnosticResult implements Serializable {

    @ApiModelProperty("x轴的名称")
    private List<String> xName;


    @ApiModelProperty("y轴的数据")
    private List<TenantStatisticsYData> yDataList;


}
