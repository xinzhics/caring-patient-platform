package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @className: PatientAbleAddFormResultDTO
 * @author: 杨帅
 * @date: 2024/4/12
 */
@Data
@ApiModel
public class PatientAbleAddFormResultDTO {


    @ApiModelProperty("患者ID")
    private Long patientId;

    @ApiModelProperty("护理计划")
    private Long planId;

    @ApiModelProperty("日期")
    private LocalDate localDate;


}
