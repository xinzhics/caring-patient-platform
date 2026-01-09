package com.caring.sass.nursing.service.plan;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.follow.PatientMenuFollow;
import com.caring.sass.nursing.dto.follow.PatientMenuFollowItem;
import com.caring.sass.nursing.dto.plan.SubscribeDTO;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.plan.PatientCustomPlanTime;
import com.caring.sass.nursing.entity.plan.PatientNursingPlan;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.entity.plan.PlanDetailTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 会员订阅护理计划
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
public interface PatientNursingPlanService extends SuperService<PatientNursingPlan> {

//    List<PatientNursingPlan> findList(LbqWrapper<PatientNursingPlan> lbqWrapper);


    SubscribeDTO getSubscribeList(Long patientId);

    /**
     * 查询患者订阅的护理计划
     * @param patientId
     * @return
     */
    List<PatientNursingPlan> patientSubscribeList(Long patientId);

    void updateMyNursingPlans(Long patientId, SubscribeDTO subscribeDTO);

    void setPatientCustomPlanTime(SubscribeDTO subscribeDTO);

    /**
     * 计算患者入组后。计划首次的执行日期
     * @param completeEnterGroupTime
     * @param detailTime
     * @param execute
     * @return
     */
    LocalDate getPatientFirstRemindDay(LocalDateTime completeEnterGroupTime, PlanDetailTime detailTime, Integer execute, Integer frequency);

    /**
     * 推算出 患者最近的下次执行时间是什么时候
     * @param completeEnterGroupTime
     * @param detailTime
     * @param execute
     * @param frequency
     * @param currentDay
     * @return
     */
    LocalDateTime getPatientNextRemindTime(LocalDateTime completeEnterGroupTime,
                                           PlanDetailTime detailTime,
                                           Integer execute,
                                           Integer frequency,
                                           LocalDate currentDay, Integer effectiveTime);

    /**
     * 取消患者对护理计划的订阅
     * @param patientId
     * @param nursingPlantId
     */
    void cancelPatientNursingPlan(Long patientId, Long nursingPlantId);

    /**
     * 患者单独订阅某护理计划
     * @param patientId
     * @param planId
     */
    void subscribePlan(Long patientId, Long planId);


    void handleInjectionForm(Plan plan, FormResult formResult);

    /**
     * 查询患者自定义的
     * @param patientId
     * @param planId
     * @param detailId
     * @param detailTimeId
     */
    PatientCustomPlanTime getPatientCustomPlan(Long patientId, Long planId, Long detailId, Long detailTimeId);


    /**
     * 查询患者随访日历部分的数据
     * @param patientId
     * @return
     */
    PatientMenuFollow patientMenuFollow(Long patientId, Long doctorId);


    /**
     * 患者今日待办
     * @param patientId 患者ID
     * @param menuController 是否需要菜单控制
     * @return
     */
    List<PatientMenuFollowItem> patientMenuFollow(Long patientId, boolean menuController);
    /**
     * 患者首次入组后。默认绑定所有的随访计划
     * @param patientId
     */
    void patientFirstSubscribePlanAll(Long patientId);



    void queryRegisterCompletePlanAndPush(String tenant, Long patientId);


    void patientBindPlan(String tenant, Long planId);



}
