package com.caring.sass.nursing.service.follow;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.follow.*;
import com.caring.sass.nursing.entity.follow.FollowTask;
import com.caring.sass.nursing.entity.follow.FollowTaskContent;
import com.caring.sass.nursing.entity.plan.Plan;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 业务接口
 * 随访任务
 * </p>
 *
 * @author 杨帅
 * @date 2023-01-04
 */
public interface FollowTaskService extends SuperService<FollowTask> {


    /**
     * 初始化系统现在所有项目的随访
     */
    void initAllTenantFollow();


    FollowTask initOneTenantFollow();

    /**
     * 更新随访任务基础和内容
     * @param followTask
     * @param contentList
     */
    void updateById(FollowTask followTask, List<FollowTaskContent> contentList);


    FollowTaskUpdateDTO getFollowTask();


    FollowBriefIntroductionDTO findFollowBriefIntroduction();

    /**
     * app查看的随访的简介
     * @return
     */
    FollowBriefIntroductionDTO appFindFollowBriefIntroduction(List<Long> tagIdList);

    /**
     * 医生端查看的随访的简介
     * @return
     */
    FollowBriefIntroductionDTO doctorFindFollowBriefIntroduction(List<Long> tagIdList);

    /**
     * 获取患者可见的 护理计划
     * @param patientId
     * @return
     */
    List<Plan> getPatientSeePlan(Long patientId);

    /**
     * 患者端的随访简介
     * @param patientId
     * @return
     */
    FollowBriefIntroductionDTO patientFindFollowBriefIntroduction(Long patientId);

    /**
     * APP查询全部随访任务的安排
     * @return
     */
    FollowAllPlanDTO findFollowPlan(Integer currentPage, List<Long> tagId);


    /**
     * 查询学习计划的推送计划安排
     * @return
     * @param tagId
     */
    List<FollowPlanShowDTO> findLearnPlan(List<Long> tagId);

    /**
     * 查询其他计划的推送计划安排
     * @return
     */
    FollowPlanShowDTO findOtherPlan(Long planId, Integer currentPage);


    /**
     * 修改随访中内容的显示和隐藏
     * @param followTaskContentUpdateDTO
     */
    void updateFollowContentShowStatus(FollowTaskContentUpdateDTO followTaskContentUpdateDTO);


    /**
     * 查询 app 随访统计页面的简介
     *
     * @return
     */
    FollowCountDetailDTO queryFollowCountDetail();


    /**
     * 查询 其他计划推送的统计的详细情况
     *
     * @return
     * @param followTaskContentId
     */
    FollowCountOtherPlanDTO queryCountOtherPush(Long followTaskContentId);


    /**
     * 查询 其他计划推送的统计的详细情况
     *
     * @return
     * @param followTaskContentId
     */
    FollowCountOtherPlanDTO doctorQueryCountOtherPush(Long followTaskContentId);


    /**
     * app 查询 学习计划随访统计
     * @return
     */
    List<FollowCountLearnPlanDto> queryCountLearnPlanPush();


    /**
     * 医生 查询 学习计划随访统计
     * @return
     */
    List<FollowCountLearnPlanDto> doctorQueryCountLearnPlanPush();


    /**
     * 查询患者全部 或 指定的随访的执行计划
     * @param patientId
     * @param planId
     * @return
     */
    List<FollowAllExecutionCycleDTO> patientQueryFollowPlanUnExecuted(Long patientId, Long planId);


    /**
     * 查询患者学习计划 的列表
     * @param patientId
     * @return
     */
    List<FollowPlanShowDTO> patientQueryLearnPlan(Long patientId);


    /**
     * 查询已执行计划
     * @param patientId
     * @param planId
     * @return
     */
    List<FollowAllExecutionCycleDTO> patientQueryFollowPlanExecuted(Long patientId, Long planId);

    /**
     * 计算患者 在这个月份。哪些天有随访
     * @param patientId
     * @param planId
     * @return
     */
    Set<LocalDate> patientQueryFollowPlanCalendar(Long patientId, Long planId, Integer learnPlan, LocalDate queryMonthly);

    /**
     * 查询日历某一天的随访任务
     * @param patientId
     * @param planId
     * @return
     */
    FollowAllExecutionCycleDTO patientQueryFollowPlanCalendarDayPlanDetail(Long patientId, Long planId, Integer learnPlan, LocalDate queryMonthly);


    /**
     * 患者端 查询随访统计的简介
     *
     * @param patientId
     * @return
     */
    FollowCountDetailDTO patientQueryFollowCountDetail(Long patientId);

    /**
     * 患者端 查询学习计划的 统计
     *
     * @param patientId
     * @return
     */
    List<PatientFollowLearnPlanStatisticsDTO> patientLearnPlanStatistics(Long patientId);

    /**
     * 患者查询一个计划的自己的推送情况
     * @param patientId
     * @param planId
     * @return
     */
    FollowCountOtherPlanDTO patientFollowPlanStatistics(Long patientId, Long planId);
}
