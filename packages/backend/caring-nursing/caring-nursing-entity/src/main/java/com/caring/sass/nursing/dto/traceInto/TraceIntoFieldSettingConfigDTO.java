package com.caring.sass.nursing.dto.traceInto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @className: TraceIntoFieldSettingConfigDTO
 * @author: 杨帅
 * @date: 2023/8/8
 */
@Data
public class TraceIntoFieldSettingConfigDTO {

    @ApiModelProperty("随访计划的列表")
    private List<PlanFormDTO> planFormDTOList;


}
