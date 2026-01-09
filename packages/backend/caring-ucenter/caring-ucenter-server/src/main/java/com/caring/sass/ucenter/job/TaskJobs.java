package com.caring.sass.ucenter.job;

import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.user.service.KeywordSettingService;
import com.caring.sass.user.service.StatisticsDoctorService;
import com.caring.sass.user.service.StatisticsNursingStaffService;
import com.caring.sass.user.service.redis.DoctorTimeOutConsultationNotice;
import com.caring.sass.user.service.redis.FollowUpNotRegisterCallBackCenter;
import com.caring.sass.user.service.redis.PatientMsgsNoReadCenter;
import com.caring.sass.utils.SpringUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 任务调动中心配置cron规则 {@see http://39.106.72.188:8767/xxl-job-admin/jobinfo}
 *
 * @author xinzh
 */
@Slf4j
@Component
public class TaskJobs {

    private final StatisticsNursingStaffService nursingStaffService;

    private final StatisticsDoctorService statisticsDoctorService;

    private final KeywordSettingService keywordSettingService;

    private final FollowUpNotRegisterCallBackCenter followUpNotRegisterCallBackCenter;

    private final DoctorTimeOutConsultationNotice doctorTimeOutConsultationNotice;

    private final PatientMsgsNoReadCenter patientMsgsNoReadCenter;

    public TaskJobs(StatisticsNursingStaffService nursingStaffService,
                    StatisticsDoctorService statisticsDoctorService,
                    FollowUpNotRegisterCallBackCenter followUpNotRegisterCallBackCenter,
                    DoctorTimeOutConsultationNotice doctorTimeOutConsultationNotice,
                    PatientMsgsNoReadCenter patientMsgsNoReadCenter,
                    KeywordSettingService keywordSettingService) {
        this.nursingStaffService = nursingStaffService;
        this.statisticsDoctorService = statisticsDoctorService;
        this.patientMsgsNoReadCenter = patientMsgsNoReadCenter;
        this.keywordSettingService = keywordSettingService;
        this.followUpNotRegisterCallBackCenter = followUpNotRegisterCallBackCenter;
        this.doctorTimeOutConsultationNotice = doctorTimeOutConsultationNotice;
    }

    /**
     * 0 0/10 * * * ?
     * 定时拉取 患者最晚回复时间。
     * 如果患者最晚回复时间等于当前时间，则发送短信提醒
     */
    @XxlJob("scheduledPushPatientMsgsNoRead")
    public ReturnT<String> scheduledPushPatientMsgsNoRead(String param) {
        log.error("[scheduledPushPatientMsgsNoRead] 定时任务开始 执行");
        patientMsgsNoReadCenter.scheduledPushPatientMsgsNoRead();
        return ReturnT.SUCCESS;
    }

    /**
     * syncNursingStaffFromDb 每天零点同步统计信息
     */
    @XxlJob("syncNursingStaffFromDb")
    public ReturnT<String> syncNursingStaffFromDb(String param) {
        log.error("[syncNursingStaffFromDb] 定时任务开始 执行");
        nursingStaffService.syncFromDb();
        return ReturnT.SUCCESS;
    }

    /**
     * syncDoctorFromDb 每天零点同步统计信息
     */
    @XxlJob("syncDoctorFromDb")
    public ReturnT<String> syncDoctorFromDb(String param) {
        log.error("[syncDoctorFromDb] 定时任务开始 执行");
        statisticsDoctorService.syncFromDb();
        return ReturnT.SUCCESS;
    }

    /**
     * 同步关键字每日的触发次数。7 天和30天的要各减最早的一天
     * @param param
     * @return
     */
    @XxlJob("syncUpdateKeywordLeaderboard")
    public ReturnT<String> syncUpdateKeywordLeaderboard(String param) {
        log.error("[syncUpdateKeywordLeaderboard] 定时任务开始 执行");
        keywordSettingService.syncUpdateKeywordLeaderboard();
        return ReturnT.SUCCESS;
    }

    /**
     * 患者未关注提醒
     *
     * @param param
     * @return
     */
    // TODO: 需要区分一下。个人服务号， 企业服务号的区别
    @XxlJob("patientUnRegisteredReply")
    public ReturnT<String> patientUnRegisteredReply(String param) {
        log.error("[patientUnRegisteredReply] 定时任务开始 执行");
        followUpNotRegisterCallBackCenter.handleTask();
        return ReturnT.SUCCESS;
    }

    /**
     * 初始化所有医生的 名片
     * @param param
     * @return
     */
    @XxlJob("initDoctorBusinessCardQrCode")
    public ReturnT<String> initDoctorBusinessCardQrCode(String param) {
//        doctorService.initDoctorBusinessCardQrCode();
        return ReturnT.SUCCESS;
    }


    /**
     * 回复超过指定时间没有被回复的患者消息
     * @param param
     * @return
     */
    @XxlJob("replyPatientMessage")
    public ReturnT<String> replyPatientMessage(String param) {

        ImApi bean = SpringUtils.getBean(ImApi.class);
        bean.replyPatientMessage();
        return ReturnT.SUCCESS;
    }


    /**
     * 任务触发时间 早上8点 晚上18点
     * 5.2.1 一日两次通知短信 （咨询通知）
     *       1. 患者发起的咨询消息(一天中午一条，晚上一条)
     * @param param
     * @return
     */
    @XxlJob("doctor_time_out_consultation_notice")
    public ReturnT<String> doctor_time_out_consultation_notice(String param) {

        LocalDateTime now = LocalDateTime.now();
        now = now.plusHours(-12);
        doctorTimeOutConsultationNotice.handlePatientMessage(now);

        return ReturnT.SUCCESS;
    }


    /**
     * 个人服务号医助长时间未回复患者消息提醒
     * @param param
     * @return
     */
    @XxlJob("nursing_time_out_consultation_notice")
    public ReturnT<String> nursingTimeOutConsultationNotice(String param) {

        LocalDateTime now = LocalDateTime.now();
        now = now.plusMinutes(-30);
        doctorTimeOutConsultationNotice.nursingHandlePatientMessage(now);

        return ReturnT.SUCCESS;
    }




}
