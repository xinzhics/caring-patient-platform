package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @className: InjectionCalendar
 * @author: 杨帅
 * @date: 2024/4/10
 */
@Data
@ApiModel("注射日历日期记录")
public class InjectionCalendar {

    @ApiModelProperty("过去注射日期")
    private List<LocalDate> localDateList;


    @ApiModelProperty("下次提醒时间")
    private LocalDate nextRemindDate;

}
