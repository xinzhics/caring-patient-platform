package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DataFeedback
 * @Description 监测指标的提示
 * @Author yangShuai
 * @Date 2022/4/29 15:28
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataFeedBack {

    @ApiModelProperty(value = "最大值")
    private Double maxValue;

    @ApiModelProperty(value = "最小值")
    private Double minValue;

    @ApiModelProperty(value = "数据反馈文字")
    private String promptText;

    @ApiModelProperty(value = "正常 1或异常 2")
    private Integer normalAnomaly;

    @ApiModelProperty(value = "正常或异常的文字")
    private String normalAnomalyText;


}
