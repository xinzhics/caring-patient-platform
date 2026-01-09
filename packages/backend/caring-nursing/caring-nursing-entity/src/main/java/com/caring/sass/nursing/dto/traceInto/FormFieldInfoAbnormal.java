package com.caring.sass.nursing.dto.traceInto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormFieldInfoAbnormal {

    @ApiModelProperty("字段ID")
    String fieldId;

    @ApiModelProperty("字段名称")
    String fieldName;

    @ApiModelProperty("异常字段的统计数据")
    List<FormFieldOptionAbnormalCount> fieldOptionAbnormalCountList = new ArrayList<>();


    public void addFieldOptionAbnormalCount(String fieldOptionId, String fieldOptionNameId, Integer count) {
        FormFieldOptionAbnormalCount abnormalCount = new FormFieldOptionAbnormalCount();
        abnormalCount.setFieldOptionId(fieldOptionId);
        abnormalCount.setFieldOptionNameId(fieldOptionNameId);
        abnormalCount.setCount(count);
        fieldOptionAbnormalCountList.add(abnormalCount);
    }

    @Data
    class FormFieldOptionAbnormalCount {

        @ApiModelProperty("字段选项ID")
        String fieldOptionId;

        @ApiModelProperty("字段选项名称ID")
        String fieldOptionNameId;

        @ApiModelProperty("统计")
        Integer count;

    }


}