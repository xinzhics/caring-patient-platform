package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;


@Data
@ApiModel
public class BusinessCardUseDayStatisticsQueryDto {

    @ApiModelProperty(value = "医生姓名")
    @Length(max = 100, message = "医生姓名长度不能超过100")
    private String doctorName;



    @ApiModelProperty(value = "名片用户的医院名称")
    @Length(max = 255, message = "名片用户的医院名称长度不能超过255")
    private String hospitalName;


    @ApiModelProperty(value = "名片用户的科室名称")
    @Length(max = 255, message = "名片用户的科室名称长度不能超过255")
    private String departmentName;


    @ApiModelProperty(value = "开始日期")
    private LocalDate startStatisticsDate;


    @ApiModelProperty(value = "结束日期")
    private LocalDate endStatisticsDate;


    @ApiModelProperty(value = "排序的列 peopleOfViews  numberOfViews  forwardingFrequency  aiDialogueNumberCount  aiDialogueClickCount ")
    private String sortClo;


    @ApiModelProperty(value = "排序类型 desc 倒序 asc 正序")
    private String sortType;

}
