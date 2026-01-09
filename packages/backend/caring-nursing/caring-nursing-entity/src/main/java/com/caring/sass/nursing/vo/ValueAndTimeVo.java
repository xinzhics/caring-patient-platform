package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "ValueAndTimeVo", description = "监测数据未处理列表Vo对象")
public class ValueAndTimeVo {
    /**
     * 表单实际填写值
     */
    @ApiModelProperty(value = "表单实际填写值")
    private String realWriteValue;
    /**
     * 表单实际填写值单位
     */
    @ApiModelProperty(value = "表单实际填写值单位")
    private String company;
    /**
     * 表单提交时间
     */
    @ApiModelProperty(value = "表单提交时间")
    private LocalDateTime formWriteTime;

    /**
     * 表单字段
     */
    @ApiModelProperty(value = "表单字段")
    private String fieldLabel;
}
