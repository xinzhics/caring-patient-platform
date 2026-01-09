package com.caring.sass.nursing.dto.follow;

import com.caring.sass.nursing.enumeration.PlanEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Data
public class FollowAllExecutionCycleDTO {

    // 需要根据 planExecutionDay 做一个从小到大的排序
    @ApiModelProperty("计划执行的天数")
    int planExecutionDay;

    @ApiModelProperty("执行日期")
    private LocalDate executionDate;

    private Map<String, FollowAllPlanDetailDTO> detailDTOMap = new HashMap<>();

    @ApiModelProperty("此天数(日期)中执行的计划目录")
    public List<FollowAllPlanDetailDTO> planDetails = new ArrayList<>();

    public FollowAllExecutionCycleDTO() {
    }

    public FollowAllExecutionCycleDTO(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public void addPLanDetailsNotLearnPlan(FollowAllPlanDetailDTO detailDTO,
                                           Long cmsId,
                                           String hrefUrl,
                                           String cmsTitle,
                                           LocalTime localTime,
                                           int cmsReadStatus,
                                           Long messageId, Long planDetailTimeId) {
        Long planId = detailDTO.getPlanId();
        FollowAllPlanDetailTimeDTO followAllPlanDetailTimeDTO = new FollowAllPlanDetailTimeDTO();
        followAllPlanDetailTimeDTO.setCmsId(cmsId);
        followAllPlanDetailTimeDTO.setHrefUrl(hrefUrl);
        followAllPlanDetailTimeDTO.setSecondShowTitle(cmsTitle);
        followAllPlanDetailTimeDTO.setPlanExecutionDate(localTime);
        followAllPlanDetailTimeDTO.setCmsReadStatus(cmsReadStatus);
        followAllPlanDetailTimeDTO.setMessageId(messageId);
        followAllPlanDetailTimeDTO.setPlanDetailTimeId(planDetailTimeId);
        if (planId != null) {
            FollowAllPlanDetailDTO planDetailDTO = detailDTOMap.get(planId.toString());
            if (Objects.isNull(planDetailDTO)) {
                planDetailDTO = detailDTO;
                planDetails.add(planDetailDTO);
                detailDTOMap.put(planId.toString(), planDetailDTO);
            }
            planDetailDTO.addPlanDetailTimeDTOS(followAllPlanDetailTimeDTO);

        } else {
            String key = "LEARN_PLAN" + PlanEnum.LEARN_PLAN.getCode();
            FollowAllPlanDetailDTO planDetailDTO = detailDTOMap.get(key);
            if (Objects.isNull(planDetailDTO)) {
                planDetailDTO = detailDTO;
                planDetails.add(planDetailDTO);
                detailDTOMap.put(key, planDetailDTO);
            }
            planDetailDTO.addPlanDetailTimeDTOS(followAllPlanDetailTimeDTO);
        }

    }

    public void addPLanDetailsNotLearnPlan(FollowAllPlanDetailDTO detailDTO,
                                           Long cmsId,
                                           String hrefUrl,
                                           String cmsTitle,
                                           LocalTime localTime,
                                           int cmsReadStatus,
                                           Long messageId) {
        addPLanDetailsNotLearnPlan(detailDTO, cmsId, hrefUrl, cmsTitle, localTime, cmsReadStatus, messageId, null);
    }

    public void addPLanDetailsNotLearnPlan(FollowAllPlanDetailDTO detailDTO,
                                           Long cmsId,
                                           String hrefUrl,
                                           String cmsTitle,
                                           LocalTime localTime) {
        this.addPLanDetailsNotLearnPlan(detailDTO, cmsId, hrefUrl,cmsTitle,localTime, 0, null);
    }


    public void sort() {
        planDetails.sort((o1, o2) -> {
            if (o1.getPlanExecutionDate() == null || o2.getPlanExecutionDate() == null) {
                return 0;
            }
            if (o1.getPlanExecutionDate().isAfter(o2.getPlanExecutionDate())) {
                return 1;
            } else {
                return -1;
            }
        });
    }

}
