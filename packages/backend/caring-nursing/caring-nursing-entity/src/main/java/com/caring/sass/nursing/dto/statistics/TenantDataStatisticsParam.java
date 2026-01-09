package com.caring.sass.nursing.dto.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * @ClassName TenantDataStatisticsParam
 * @Description
 * @Author yangShuai
 * @Date 2022/5/18 11:47
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "TenantDataStatisticsParam", description = "统计使用的参数")
@AllArgsConstructor
public class TenantDataStatisticsParam extends TenantStatisticsChartList {

    @ApiModelProperty("开始时间")
    private LocalDate startTime;

    @ApiModelProperty("结束时间")
    private LocalDate endTime;

    public Period getPeriod() {
        if (Objects.isNull(startTime) && Objects.isNull(endTime)) {
            return null;
        } else {
            return Period.between(startTime, endTime);
        }
    }

}
