package com.caring.sass.nursing.service.task;

import com.caring.sass.nursing.dto.plan.PlanDetailDTO;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 护理计划推送参数
 *
 * @author leizhi
 */
@Data
public class NursingPlanPushParam {

    List<PlanDetailDTO> nursingPlantDetails;
    Plan nursingPlan;
    NursingPlanPatientBaseInfoDTO patient;
    Tenant tenant;
    LocalDateTime date;
    /**
     * 护理计划
     */
    PlanEnum planEnum;

    TemplateMsgDto templateMsgDto;
    Map<String, TemplateMsgDto> templateMsgDtoMap;

    public NursingPlanPushParam(List<PlanDetailDTO> nursingPlantDetails,
                                Plan nursingPlan,
                                NursingPlanPatientBaseInfoDTO patient,
                                Tenant tenant,
                                LocalDateTime date,
                                PlanEnum planEnum,
                                TemplateMsgDto templateMsgDto,
                                Map<String, TemplateMsgDto> templateMsgDtoMap) {
        this.nursingPlantDetails = nursingPlantDetails;
        this.nursingPlan = nursingPlan;
        this.templateMsgDto = templateMsgDto;
        this.templateMsgDtoMap = templateMsgDtoMap;
        this.patient = patient;
        this.tenant = tenant;
        this.date = date;
        this.planEnum = planEnum;
    }

    public NursingPlanPushParam() {
    }

    public List<PlanDetailDTO> getNursingPlantDetails() {
        return nursingPlantDetails;
    }

    public Plan getNursingPlan() {
        return nursingPlan;
    }

    public NursingPlanPatientBaseInfoDTO getPatient() {
        return patient;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public PlanEnum getPlanEnum() {
        return planEnum;
    }

    public TemplateMsgDto getTemplateMsgDto() {
        return templateMsgDto;
    }

    public void setTemplateMsgDto(TemplateMsgDto templateMsgDto) {
        this.templateMsgDto = templateMsgDto;
    }

    public Map<String, TemplateMsgDto> getTemplateMsgDtoMap() {
        return templateMsgDtoMap;
    }

    public void setTemplateMsgDtoMap(Map<String, TemplateMsgDto> templateMsgDtoMap) {
        this.templateMsgDtoMap = templateMsgDtoMap;
    }
}
