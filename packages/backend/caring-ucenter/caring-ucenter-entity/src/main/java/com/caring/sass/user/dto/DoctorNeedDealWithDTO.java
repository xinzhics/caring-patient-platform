package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @className: DoctorNeedDealWithDTO
 * 医生待办
 * @author: 杨帅
 * @date: 2024/1/9
 */
@Data
@ApiModel("医生待办")
public class DoctorNeedDealWithDTO {

    private Integer imMessage;

    private Integer consultationGroup;

    private Integer appointment;

    private Integer referral;


}
