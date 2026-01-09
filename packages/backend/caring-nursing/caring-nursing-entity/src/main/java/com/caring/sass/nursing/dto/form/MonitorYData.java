package com.caring.sass.nursing.dto.form;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.nursing.entity.form.PatientFormFieldReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName MonitorYData
 * @Description
 * @Author yangShuai
 * @Date 2022/2/15 11:29
 * @Version 1.0
 */
@ApiModel("趋势图Y轴相关数据")
@Data
public class MonitorYData {

    @ApiModelProperty(value = "监测指标字段id")
    private String fieldId;

    @ApiModelProperty(value = "右侧单位")
    private String rightUnit;

    @ApiModelProperty(value = "y线名称")
    private String label;

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

    @ApiModelProperty(value = "是否显示 基准值目标值")
    private Boolean showReferenceTargetValue;

    @ApiModelProperty(value = "基准值")
    private Double referenceValue;

    @ApiModelProperty(value = "目标值")
    private Double targetValue;

    @ApiModelProperty(value = "yData的数据")
    private List<Object> yData = null;

    @ApiModelProperty("Y轴的最大值最小值和等分区间")
    private YAxis yaxis;



    public void setyAxis() {
        YAxis yAxis = new YAxis();
        Double max = null;
        Double min = null;
        Double interval;
        // 查询 用户填写的最大值
        double userInputMaxValue = 0;
        Double userInputMinValue = null;
        int userInputNumber = 0;
        boolean userInputDecimal = false;
        if (yData != null) {
            for (Object yDatum : yData) {
                if (Objects.nonNull(yDatum)) {
                    userInputNumber++;
                } else {
                    continue;
                }
                double parseDouble = Double.parseDouble(yDatum.toString());
                if (yDatum.toString().contains(".")) {
                    userInputDecimal = true;
                }
                if (parseDouble > userInputMaxValue) {
                    if (userInputMinValue == null) {
                        userInputMinValue = userInputMaxValue;
                    }
                    userInputMaxValue = parseDouble;
                }  else if (parseDouble < userInputMaxValue && userInputMinValue == null) {
                    userInputMinValue = parseDouble;
                } else if (Objects.isNull(userInputMinValue) || parseDouble < userInputMinValue) {
                    userInputMinValue = parseDouble;
                }
            }
        }
        boolean setReference = false;
        // 先将参考区间或参考的精确值设置为最大值
        if ("exact_value".equals(trendReference) && StrUtil.isNotEmpty(trendReferenceExactValue) && hasTrendReference) {
            max = Double.parseDouble(trendReferenceExactValue);
            min = Double.parseDouble(trendReferenceExactValue);
            setReference = true;
        } else if ("range".equals(trendReference) && StrUtil.isNotEmpty(trendReferenceMax)  && hasTrendReference ) {
            max = Double.parseDouble(trendReferenceMax);
            if (StrUtil.isNotEmpty(trendReferenceMin)) {
                min = Double.parseDouble(trendReferenceMin);
            } else {
                min = 0D;
            }
            setReference = true;
        }
        // 用户没有填写过表单。
        // 当用户没有填写值时，趋势图纵坐标最大值取参考值或参考区间的最大值，最小值取0，未设置参考值或参考区间时，不显示纵坐标
        if (userInputNumber == 0) {
            if (setReference) {
                min = 0D;
            }
         // 用户只填写了一个数值且未设置参考值时，纵坐标最小值取0，最大值取填写值的2倍，其余坐标依旧5等分
        } else if (userInputNumber == 1 && !setReference) {
            max = userInputMaxValue * 2;
            min = 0D;
        // 用户填写了1次甚至多次表单。
        } else {
            // 所配置参考值或参考区间大于用户所填写最大值时，纵坐标最上方最大值取参考值或参考区间的最大值，纵坐标最下方依旧取用户所填写最小值
            if (setReference) {
                if (max < userInputMaxValue) {
                    max = userInputMaxValue;
                }
                // 所配置参考值或参考区间小于用户所填写最小值是，纵坐标最下方最小值取0，纵坐标最上方依旧取用户所填写的最大值
                if (min < userInputMinValue) {
                    min = 0d;
                }  else {
                    min = userInputMinValue;
                }
            } else {
                max = userInputMaxValue;
                min = userInputMinValue;
            }
        }
        // 最大值-（最大值-最小值）/5
        if (max != null) {
            interval = (max - (max -min) /5*4) - min;
            DecimalFormat decimalFormat = new DecimalFormat("#");
            decimalFormat.setRoundingMode(RoundingMode.UP);
            String intervalFormat = decimalFormat.format(interval);
            yAxis.setInterval(intervalFormat);
            int intervalInt = Integer.parseInt(intervalFormat);
            int minResult = min.intValue();
            int maxResult = minResult + intervalInt * 5;
            yAxis.setMax(maxResult + "");
            yAxis.setMin(minResult + "");
            this.yaxis = yAxis;
        }

    }


    static class YAxis {
        String min;
        String max;
        String interval;

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }
    }

    public void addYData(Object y) {

        if (CollUtil.isEmpty(yData)) {
            yData = new ArrayList<>(30);
        }
        yData.add(y);
    }


    public void addField(FormFieldDto field, PatientFormFieldReference reference) {
        this.rightUnit = field.getRightUnit();
        this.label = field.getLabel();
        this.fieldId = field.getId();
        this.hasTrendReference = field.getHasTrendReference();
        this.trendReference = field.getTrendReference();
        this.trendReferenceExactValue = field.getTrendReferenceExactValue();
        this.trendReferenceMax = field.getTrendReferenceMax();
        this.trendReferenceMin = field.getTrendReferenceMin();
        this.showReferenceTargetValue = field.getShowReferenceTargetValue();
        if (Objects.nonNull(reference)) {
            this.referenceValue = reference.getReferenceValue();
            this.targetValue = reference.getTargetValue();
        } else {
            this.referenceValue = field.getReferenceValue();
            this.targetValue = field.getTargetValue();
        }
    }

    public void addNullYData(int needNullNumber) {

        for (int i = 0; i < needNullNumber; i++) {
            if (CollUtil.isEmpty(yData)) {
                yData = new ArrayList<>(30);
            }
            yData.add(null);
        }

    }
}
