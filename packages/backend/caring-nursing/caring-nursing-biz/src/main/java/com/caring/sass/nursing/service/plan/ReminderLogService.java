package com.caring.sass.nursing.service.plan;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.follow.CmsPushReadDetail;
import com.caring.sass.nursing.dto.follow.FollowCountOtherPlanDTO;
import com.caring.sass.nursing.dto.follow.PatientFollowLearnPlanStatisticsDTO;
import com.caring.sass.nursing.dto.statistics.TenantStatisticsResult;
import com.caring.sass.nursing.entity.plan.ReminderLog;
import com.caring.sass.nursing.enumeration.PlanEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ReminderLogService
 * @Description
 * @Author yangShuai
 * @Date 2020/10/27 13:39
 * @Version 1.0
 */
public interface ReminderLogService extends SuperService<ReminderLog> {


    void submitSuccess(Long id);

    ReminderLog createReminderLog(Long patientId, String comment, String workId, Integer type, Long planId,
                                  Long orgId, String classCode, Long doctorId, Long nursingId);

    ReminderLog createReminderLog(Long patientId, String comment, String workId, Integer type, Long planId,
                                  Long orgId, String classCode, Long doctorId, Long nursingId, LocalDateTime waitPushTime, Long planDetailId);


    void sendSuccess(Long id);

    /**
     * 根据推送记录。统计复诊率的情况
     * @param startTime
     * @param endTime
     * @param period
     * @param orgIds
     * @param doctorId
     * @param nursingId
     * @return
     */
    TenantStatisticsResult statisticsData(LocalDate startTime, LocalDate endTime, Period period, List<Long> orgIds, Long doctorId, Long nursingId);


    /**
     * 用户打开了一个表单。
     * @param messageId
     */
    void openMessage(Long messageId);

    /**
     * 统计打开人数，推送人数， 完成人数，
     * 并设置到  followCountOtherPlanDTO
     * @param orgIds
     * @param reminderLogWorkId
     * @param followCountOtherPlanDTO
     */
    void statisticsData(List<Long> orgIds, String reminderLogWorkId, FollowCountOtherPlanDTO followCountOtherPlanDTO);

    /**
     * 血压 血糖
     * 统计打开人数，推送人数， 完成人数，
     * 并设置到  followCountOtherPlanDTO
     * @param orgIds
     * @param reminderLogWorkIdList
     * @param followCountOtherPlanDTO
     */
    void statisticsData(List<Long> orgIds, List<String> reminderLogWorkIdList, FollowCountOtherPlanDTO followCountOtherPlanDTO);


    /**
     * 统计打开人数，推送人数， 完成人数，
     * 并设置到  followCountOtherPlanDTO
     * @param orgIds
     * @param
     */
    void statisticsData(List<Long> orgIds, Map<String, CmsPushReadDetail> map);

    /**
     * 用户打开了外部消息，完成打卡
     * @param messageId
     */
    void externalMessages(Long messageId);

    /**
     * 血压 血糖
     * 统计打开人数，推送人数， 完成人数，
     * 并设置到  followCountOtherPlanDTO
     * @param doctorId
     * @param reminderLogWorkIdList
     * @param followCountOtherPlanDTO
     */
    void doctorStatisticsData(Long doctorId, List<String> reminderLogWorkIdList, FollowCountOtherPlanDTO followCountOtherPlanDTO);

    /**
     * 统计打开人数，推送人数， 完成人数，
     * 并设置到  followCountOtherPlanDTO
     * @param doctorId
     * @param reminderLogWorkId
     * @param followCountOtherPlanDTO
     */
    void doctorStatisticsData(Long doctorId, String reminderLogWorkId, FollowCountOtherPlanDTO followCountOtherPlanDTO);


    /**
     * 统计打开人数，推送人数， 完成人数，
     * 并设置到  followCountOtherPlanDTO
     * @param doctorId
     * @param
     */
    void doctorStatisticsData(Long doctorId, Map<String, CmsPushReadDetail> map);


    /**
     * 患者统计 学习计划的 推送情况
     * @param patientId
     * @param workIds
     * @param statisticsDTO
     */
    void patientStatistics(Long patientId, List<String> workIds, PatientFollowLearnPlanStatisticsDTO statisticsDTO);


    /**
     * 患者统计 其他护理计划的推送
     * @param patientId
     * @param workIds
     * @param planDTO
     */
    void patientStatistics(Long patientId, List<String> workIds, FollowCountOtherPlanDTO planDTO);

    /**
     * 检查这个随访计划的 推送时间，今天是否给患者推送过。
     * @param planDetailTimeId
     * @param patientId
     * @return true 表示已经打卡，不需要再推送。
     */
    boolean checkReminderLogExits(String tenantCode, Long planDetailTimeId, String workId, Long patientId);

    /**
     * 创建提前打卡，或者更新打卡状态
     * @param planDetailTimeId
     * @param patientId
     * @param messageId
     */
    Long createOrUpdateReminderLog(Long planDetailTimeId, PlanEnum planType, Long patientId, Long messageId);

    /**
     * 创建提前打卡，或者更新打卡状态
     * @param planDetailTimeId
     * @param patientId
     * @param messageId
     */
    Long createOrUpdateReminderLog(Long planDetailTimeId, Long patientId, Long messageId);

    Long queryRecentlyUnReadMessage(Long patientId, Long planId);

    /**
     * 查询需要发送的记录
     * @param localDateTime
     * @return
     */
    List<String> queryNeedSendLog(LocalDateTime localDateTime);

}
