package com.caring.sass.nursing.dto.appointment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName AppointWeekTotalVo
 * @Description
 * @Author yangShuai
 * @Date 2021/1/27 15:51
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("每周统计VO")
public class AppointWeekTotalVo {

    @ApiModelProperty(value = "周一日期")
    private Date mondayDay;

    @ApiModelProperty(value = "周一预约总数")
    private Integer mondayUserTotal;

    @ApiModelProperty(value = "周二日期")
    private Date tuesdayDay;

    @ApiModelProperty(value = "周二预约总数")
    private Integer tuesdayUserTotal;

    @ApiModelProperty(value = "周三日期")
    private Date wednesdayDay;

    @ApiModelProperty(value = "周三预约总数")
    private Integer wednesdayUserTotal;

    @ApiModelProperty(value = "周四日期")
    private Date thursdayDay;

    @ApiModelProperty(value = "周四预约总数")
    private Integer thursdayUserTotal;

    @ApiModelProperty(value = "周五日期")
    private Date fridayDay;

    @ApiModelProperty(value = "周五预约总数")
    private Integer fridayUserTotal;

    @ApiModelProperty(value = "周六日期")
    private Date saturdayDay;

    @ApiModelProperty(value = "周六预约总数")
    private Integer saturdayUserTotal;

    @ApiModelProperty(value = "周日日期")
    private Date sundayDay;

    @ApiModelProperty(value = "周日预约总数")
    private Integer sundayUserTotal;


}
