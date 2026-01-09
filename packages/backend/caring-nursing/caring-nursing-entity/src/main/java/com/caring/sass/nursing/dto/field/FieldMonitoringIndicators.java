package com.caring.sass.nursing.dto.field;

import com.alibaba.fastjson.annotation.JSONField;
import com.caring.sass.nursing.dto.form.DataFeedBack;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FieldMonitoringIndicators
 * @Description
 * @Author yangShuai
 * @Date 2022/3/16 13:32
 * @Version 1.0
 */
@Data
@ApiModel(value = "FieldMonitoringIndicators", description = "监测指标字段")
public class FieldMonitoringIndicators {


    @ApiModelProperty(value = "字段属性的唯一标识")
    String id;

    @ApiModelProperty(value = "标题")
    String label;

    @ApiModelProperty(value = "标题的描述")
    String labelDesc;

    @ApiModelProperty(value = "是否必填")
    Boolean required;

    @ApiModelProperty(value = "字段标识 monitoringIndicators", dataType = "FormFieldExactType")
    String exactType;

    @ApiModelProperty(value = "字段类型", dataType = "FormWidget")
    FormWidget widgetType;

    @ApiModelProperty(value = "医助可以修改（1： 是 ）", dataType = "ispdatable")
    Integer isUpdatable;

    @ApiModelProperty(value = "输入时的提示")
    String placeholder;

    @ApiModelProperty(value = "右侧单位")
    String rightUnit;

    @ApiModelProperty(value = "最小值")
    String minValue;

    @ApiModelProperty(value = "最大值")
    String maxValue;

    @ApiModelProperty(value = "数字类型,支持小数")
    Integer canDecimal;

    @ApiModelProperty(value = "保留小数，后几位")
    Integer precision;

    @ApiModelProperty(value = "趋势图，1 是， null 0 否")
    private Integer showTrend;

    @ApiModelProperty(value = "趋势图参考值是否有参考值 true 有， false 没有")
    private Boolean hasTrendReference;

    @ApiModelProperty(value = "趋势图参考值，exact_value 精确数值， range 区间范围")
    private String trendReference;

    @ApiModelProperty(value = "趋势图 精确数值")
    private String trendReferenceExactValue;

    @ApiModelProperty(value = "趋势图 区间范围参考最大值")
    private String trendReferenceMax;

    @ApiModelProperty(value = "趋势图 区间范围参考最小值")
    private String trendReferenceMin;

    @ApiModelProperty(value = "是否显示基准值和目标值")
    private Boolean showReferenceTargetValue;

    @ApiModelProperty(value = "第一次填写作为基准值")
    private Boolean firstWriteAsReferenceValue;

    @ApiModelProperty(value = "基准值")
    private Double referenceValue;

    @ApiModelProperty(value = "目标值")
    private Double targetValue;

    @ApiModelProperty(value = "显示数据反馈")
    Boolean showDataFeedback;

    @ApiModelProperty(value = "数据反馈内容")
    List<DataFeedBack> dataFeedBacks;

    @ApiModelProperty(value = "字段的结果值")
    @JSONField(jsonDirect=true)
    String values = "[" +
            "{" +
            "\"attrValue\":\"\"" +
            "}" +
            "]";


}
