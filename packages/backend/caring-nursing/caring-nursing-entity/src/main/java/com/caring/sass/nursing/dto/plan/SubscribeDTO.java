package com.caring.sass.nursing.dto.plan;

import com.alibaba.fastjson.annotation.JSONField;
import com.caring.sass.nursing.entity.plan.PatientNursingPlan;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xinzh
 */
@Data
public class SubscribeDTO {

    private List<PatientNursingPlan> subscribeList;

    private String imgUrl;

    @JSONField(format = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @ApiModelProperty(name = "患者可能的入组时间")
    private LocalDateTime completeEnterGroupTime;

    private Long patientId;

}
