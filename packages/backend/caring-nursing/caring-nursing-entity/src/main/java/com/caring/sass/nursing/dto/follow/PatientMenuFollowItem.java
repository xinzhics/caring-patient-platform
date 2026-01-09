package com.caring.sass.nursing.dto.follow;

import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

/**
 * @className: PatientMenuFollowItem
 * @author: 杨帅
 * @date: 2023/7/2
 */
@Builder
@Data
@ApiModel("随访日历的目录")
public class PatientMenuFollowItem {

    @ApiModelProperty("计划随访时间")
    private LocalTime followDateTime;

    @ApiModelProperty("提醒的内容")
    private String remindContent;

    @ApiModelProperty("随访设置的显示名称")
    private String name;

    @ApiModelProperty("当复查提醒，健康日志，指标监测，自定义监测, 提交表单结果 学习计划 提前打卡时，提供的打卡标记")
    private Long planDetailTimeId;

    @ApiModelProperty("学习计划查看的文章的ID")
    private Long cmsId;

    @ApiModelProperty("学习计划跳转的外部链接")
    private String cmsContentUrl;

    @ApiModelProperty("随访的功能名称")
    private String functionTypeName;

    @ApiModelProperty("随访的功能类型")
    private PlanFunctionTypeEnum functionTypeEnum;

    @ApiModelProperty("超时：1， 0：未超时")
    private Integer timeOut;

    @ApiModelProperty("0 未打卡， 1：已打卡")
    int cmsReadStatus = 0;

    @ApiModelProperty("计划的原始名称")
    private String planName;

    @ApiModelProperty("血压，指标监测，自定义随访")
    private Long planId;

    @ApiModelProperty("如果已经打过卡。使用messageId进入表单填写页面，可以查询到对应的表单结果")
    private Long messageId;


}
