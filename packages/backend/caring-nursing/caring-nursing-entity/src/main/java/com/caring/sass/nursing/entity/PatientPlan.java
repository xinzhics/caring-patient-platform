package com.caring.sass.nursing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.nursing.dto.form.FormField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @className: PatientPlan
 * @author: 杨帅
 * @date: 2023/12/27
 */
@Data
public class PatientPlan {

    @ApiModelProperty("计划ID")
    Long planId;

    @ApiModelProperty(value = "名称 必传")
    private String name;

    @ApiModelProperty(value = "护理计划类型（1血压监测 2血糖监测 null 就是自定义监测计划）")
    private Integer planType;

    @ApiModelProperty(value = "订阅状态")
    private Boolean subscribe;

    @ApiModelProperty(value = "表单结果ID")
    private Long formResultId;

    @ApiModelProperty(value = "评分问卷")
    private Integer scoreQuestionnaire;

    @ApiModelProperty(value = "血糖值")
    private Float sugarValue;

    @ApiModelProperty(value = "记录的日期(非填写日期) 比如 2020年1月1日0时0分0秒的 13位时间戳")
    public Long createDay;

    @ApiModelProperty(value = "血糖的类型")
    public Integer sugarType;

    @ApiModelProperty(value = "表单中的数字类型字段")
    private List<FormField> formFields;

    @ApiModelProperty(value = "数据反馈的结果JSON, null不用显示")
    private String dataFeedBack;

    @ApiModelProperty(value = "表单结果的创建时间")
    private LocalDateTime formResultCreateTime;

    @ApiModelProperty(value = "是否显示总分")
    private Integer showFormResultSumScore;

    @ApiModelProperty(value = "总分 或 总分和+平均分")
    private Float formResultSumScore;

    @ApiModelProperty(value = "是否显示平均分")
    private Integer showFormResultAverageScore;

    @ApiModelProperty(value = "平均分")
    private Float formResultAverageScore;

}
