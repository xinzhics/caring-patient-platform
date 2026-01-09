package com.caring.sass.nursing.job;

import com.caring.sass.base.R;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.appointment.AppointmentService;
import com.caring.sass.nursing.service.drugs.DrugsConditionMonitoringService;
import com.caring.sass.nursing.service.information.CompletionInformationStatisticsService;
import com.caring.sass.nursing.service.task.TaskFacade;
import com.caring.sass.nursing.service.unfinished.UnfinishedFormSettingService;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 任务调动中心配置cron规则 {@see http://39.106.72.188:8767/xxl-job-admin/jobinfo}
 *
 * @author xinzh
 */
@Slf4j
@Component
@AllArgsConstructor
public class TaskJobs {


    private final TaskFacade taskFacade;

    private final TenantApi tenantApi;

    private final CompletionInformationStatisticsService statisticsService;

    private final DrugsConditionMonitoringService drugsConditionMonitoringService;

    private final AppointmentService appointmentService;

    private final UnfinishedFormSettingService unfinishedFormSettingService;


    /**
     * 3.11 定时任务 【预约审核过期】 每分钟执行一次
     * @param param
     * @return
     */
    @XxlJob("appointmentReviewExpired")
    public ReturnT<String> appointmentReviewExpired(String param) {
        log.info("appointmentReviewExpired task start");
        LocalDateTime localDateTime = LocalDateTime.now();
        appointmentService.appointmentReviewExpired(localDateTime);
        log.info("appointmentReviewExpired task start");
        return ReturnT.SUCCESS;
    }


    /**
     * 个人服务号
     * 医生今日待办 - 预约审核提醒
     * @param param
     * @return
     */
    @XxlJob("doctor_today_to_do_list")
    public ReturnT<String> doctorTodayToDoList(String param) {
        log.info("doctor_today_to_do_list task start");
        appointmentService.doctorAppointmentReviewReminder();
        log.info("doctor_today_to_do_list task start");
        return ReturnT.SUCCESS;
    }



    /**
     * @title 就诊过期 每晚8点 执行
     * @author 杨帅 
     * @updateTime 2023/3/30 17:41
     * @throws 
     */
    @XxlJob("appointmentVisitExpired")
    public ReturnT<String> appointmentVisitExpired(String param) {
        log.info("appointmentVisitExpired task start");
        LocalDate localDate = LocalDate.now().plusDays(1);
        appointmentService.appointmentVisitExpired(localDate);
        log.info("appointmentVisitExpired task start");
        return ReturnT.SUCCESS;
    }



    /**
     * 任务执行时间 0点0分0秒
     *
     * @Description 将符合条件的药品设置为 历史药品、
     */
    @XxlJob("stopPatientDrugsJobHandler")
    public ReturnT<String> stopPatientDrugsJobHandler(String param) {
        log.info("stopPatientDrugs任务开始执行");
        taskFacade.stopPatientDrugsTask();
        return ReturnT.SUCCESS;
    }

    /**
     * 为符合吃药条件的用户 推送模板消息，提醒用药打卡
     * 只能给企业服务号推送
     */
    @XxlJob("pushDrugs")
    public ReturnT<String> pushDrugs(String param) {
        log.error("[pushDrugs] 定时任务开始 执行");
        taskFacade.pushDrugs(LocalDateTime.now());
        return ReturnT.SUCCESS;
    }


    /**
     * 直接检测  ReminderLog 表中是否存在当前时刻需要推送的记录
     * @return
     */
    @XxlJob("checkReminderLog")
    public ReturnT<String> checkReminderLog(String param) {
        log.info("[checkReminderLog] 定时任务开始 执行");
        LocalDateTime localDateTime = LocalDateTime.now().withSecond(0).withNano(0);
        taskFacade.checkReminderLog(localDateTime);
        return ReturnT.SUCCESS;
    }


    /**
     * @return void
     * @Author yangShuai
     * @Description 血压提醒 凌晨1点执行。只生产任务，不进行推送
     * @Date 2020/10/26 15:40
     */
    @XxlJob("bloodPressure")
    public ReturnT<String> bloodPressure(String param) {
        log.info("[bloodPressure] 定时任务开始 执行");
        taskFacade.nursingPlanPush(PlanEnum.BLOOD_PRESSURE, LocalDateTime.now());
        return ReturnT.SUCCESS;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 血糖提醒 凌晨1点执行。只生产任务，不进行推送
     * @Date 2020/10/26 15:41
     */
    @XxlJob("bloodGlucose")
    public ReturnT<String> bloodGlucose(String param) {
        log.error("[bloodGlucose] 血糖监测定时任务开始 执行");
        taskFacade.nursingPlanPush(PlanEnum.BLOOD_SUGAR, LocalDateTime.now());
        return ReturnT.SUCCESS;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 复查提醒 凌晨12点半点执行。只生产任务，不进行推送
     * @Date 2020/10/26 15:41
     */
    @XxlJob("reviewReminder")
    public ReturnT<String> reviewReminder(String param) {
        log.error("[reviewReminder] 定时任务开始 执行");
        taskFacade.nursingPlanPush(PlanEnum.REVIEW_REMIND, LocalDateTime.now());
        return ReturnT.SUCCESS;
    }

    /**
     * 健康日志 凌晨12点半点执行。只生产任务，不进行推送
     */
    @XxlJob("healthLog")
    public ReturnT<String> healthLog(String param) {
        log.error("[healthLog] 定时任务开始 执行");
        taskFacade.nursingPlanPush(PlanEnum.HEALTH_LOG, LocalDateTime.now());
        return ReturnT.SUCCESS;
    }

    /**
     * @return void
     * @Author yangShuai
     * @Description 学习计划 凌晨2点执行。只生产任务，不进行推送
     * @Date 2020/10/26 15:42
     */
    @XxlJob("studyPlan")
    public ReturnT<String> studyPlan(String param) {
        log.error("[studyPlan] 定时任务开始 执行");
        taskFacade.learnPlanPush(LocalDateTime.now());
        return ReturnT.SUCCESS;
    }


    /**
     * @return void
     * @Author yangShuai
     * @Description 指标监测 和 自定义随访 凌晨2点执行。只生产任务，不进行推送
     * @Date 2020/10/26 15:43
     */
    @XxlJob("customCarePlan")
    public ReturnT<String> customCarePlan(String param) {
        log.error("[customCarePlan] 定时任务开始 执行");
        taskFacade.customPlanPush(LocalDateTime.now());
        return ReturnT.SUCCESS;
    }


    /**
     * @return void
     * @Author yangShuai
     * 直接推送提醒的任务
     *
     * @Description 患者自定义推送计划
     * @Date 2020/10/26 15:45
     */
    @XxlJob("patientCustomPlan")
    public ReturnT<String> patientCustomPlan(String param) {
        LocalDateTime dateTime = LocalDateTime.now();
        taskFacade.patientCustomPlan(dateTime);
        return ReturnT.SUCCESS;
    }


    /**
     * @return void
     * @Author yangShuai
     * 直接推送提醒的任务
     *
     * @Description 患者注射计划超时
     * @Date 2020/10/26 15:45
     */
    @XxlJob("injectionFormTimeOutRemind")
    public ReturnT<String> injectionFormTimeOutRemind(String param) {
        LocalDateTime dateTime = LocalDateTime.now();
        taskFacade.injectionFormTimeOutRemind(dateTime);
        return ReturnT.SUCCESS;
    }


    /**
     * 微信 群发 图文
     *
     * @param params
     * @return
     */
    @XxlJob("executeCmsMassMailing")
    public ReturnT<String> executeCmsMassMailing(String params) {
        taskFacade.executeCmsMassMailing();
        return ReturnT.SUCCESS;
    }

    /**
     * 47小时未注册推送
     *
     * @param params
     * @return
     */
    @Deprecated
    @XxlJob("unregisteredReminder")
    public ReturnT<String> unregisteredReminder(String params) {
        taskFacade.unregisteredReminder();
        return ReturnT.SUCCESS;
    }


    /**
     * 统计患者信息完成度
     */
    @XxlJob("handleMessage")
    public ReturnT<String> handleMessage(String param) {
        taskFacade.handleMessage();
        return ReturnT.SUCCESS;
    }

    /**
     * 统计患者信息完成度
     */
    @XxlJob("handleDrugsMessage")
    public ReturnT<String> handleDrugsMessage(String param) {
        taskFacade.handleDrugsMessage();
        return ReturnT.SUCCESS;
    }

    /**
     * 统计患者信息完成度
     */
    @XxlJob("statisticsCompletenessInformationTask")
    public ReturnT<String> statisticsCompletenessInformationTask(String param) {
        statisticsService.statisticsCompletenessInformationTask(LocalDate.now());
        return ReturnT.SUCCESS;
    }

    /**
     * 每天凌晨1点生成购药|逾期预警消息
     */
    @XxlJob("createBuyDrugsWarningTask")
    public ReturnT<String> createBuyDrugsWarningTask(String param) {
        XxlJobLogger.log("【开始】每天凌晨1点生成购药|逾期预警消息 param={}",param);
        R<List<Tenant>> listR = tenantApi.getAllNormalTenant();
        if (!listR.getIsSuccess()) {
            // 如果查询失败，记录错误日志
            return ReturnT.SUCCESS;
        }
        List<Tenant> tenantList = listR.getData();
        if (Objects.isNull(tenantList) || tenantList.isEmpty()) {
            // 如果返回的数据为空或空列表，直接返回空列表
            log.warn("No tenants found for CERTIFICATION_SERVICE_NUMBER");
            return ReturnT.SUCCESS;
        }
        for (Tenant tenant : tenantList) {
            drugsConditionMonitoringService.synDrugsConditionMonitoringTask(tenant.getCode(),false);
        }
        XxlJobLogger.log("【结束】每天凌晨1点生成购药|逾期预警消息 param={}",param);
        return ReturnT.SUCCESS;
    }

    /**
     * 每天晚上0点 更新未处理的 余药不足购药|逾期预警 的消息。
     */
    @XxlJob("updateBuyDrugsWarningTask")
    public ReturnT<String> updateBuyDrugsWarningTask(String param) {
        XxlJobLogger.log("【开始】每天凌晨0点更新未处理的购药|逾期预警消息 param={}",param);
        R<List<Tenant>> listR = tenantApi.getAllNormalTenant();
        if (!listR.getIsSuccess()) {
            // 如果查询失败，记录错误日志
            return ReturnT.SUCCESS;
        }
        List<Tenant> tenantList = listR.getData();
        if (Objects.isNull(tenantList) || tenantList.isEmpty()) {
            // 如果返回的数据为空或空列表，直接返回空列表
            log.warn("No tenants found for CERTIFICATION_SERVICE_NUMBER");
            return ReturnT.SUCCESS;
        }
        for (Tenant tenant : tenantList) {
            drugsConditionMonitoringService.synUpdateBuyDrugsWarningTask(tenant.getCode());
        }
        XxlJobLogger.log("【结束】每天凌晨0点更新未处理的购药|逾期预警消息 param={}",param);
        return ReturnT.SUCCESS;
    }

    /**
     * 	每天8点推送购药|逾期预警模板消息
     */
    @XxlJob("pushBuyDrugsWarningMsgTask")
    public ReturnT<String> pushBuyDrugsWarningMsgTask(String param) {
        XxlJobLogger.log("【开始】每天8点推送购药|逾期预警模板消息 param={}",param);

        R<List<Tenant>> listR = tenantApi.getAllNormalTenant();
        if (!listR.getIsSuccess()) {
            // 如果查询失败，记录错误日志
            return ReturnT.SUCCESS;
        }
        List<Tenant> tenantList = listR.getData();
        if (Objects.isNull(tenantList) || tenantList.isEmpty()) {
            // 如果返回的数据为空或空列表，直接返回空列表
            log.warn("No tenants found for CERTIFICATION_SERVICE_NUMBER");
            return ReturnT.SUCCESS;
        }
        XxlJobLogger.log("【结束】每天8点推送购药|逾期预警模板消息 租户为空 tenantList={}",tenantList);
        if (CollectionUtils.isEmpty(tenantList)) {
            return ReturnT.SUCCESS;
        }
        for (Tenant tenant : tenantList) {
            drugsConditionMonitoringService.pushBuyDrugsWarningMsgTask(tenant);
        }
        XxlJobLogger.log("【结束】每天8点推送购药|逾期预警模板消息 param={}",param);
        return ReturnT.SUCCESS;
    }

    /**
     * 未完成任务提醒
     * @param param
     * @return
     */
    @XxlJob("unFinishedReminder")
    public ReturnT<String> unFinishedReminder(String param) {
        XxlJobLogger.log("【开始】每天7.30点执行 | 未完成推送 param={}",param);
        List<Tenant> tenantList = getAllNormalTenant();
        if (CollectionUtils.isEmpty(tenantList)) {
            return ReturnT.SUCCESS;
        }
        for (Tenant tenant : tenantList) {
            unfinishedFormSettingService.xxlJobUnFinishedTask(tenant.getCode());
        }
        return ReturnT.SUCCESS;
    }
    /**
     * 获取所有的正常的项目
     */
    protected List<Tenant> getAllNormalTenant() {
        R<List<Tenant>> listR = tenantApi.queryTenantList(TenantOfficialAccountType.CERTIFICATION_SERVICE_NUMBER);
        if (!listR.getIsSuccess()) {
            // 如果查询失败，记录错误日志
            return new ArrayList<>(); // 返回空列表
        }

        List<Tenant> tenants = listR.getData();

        if (Objects.isNull(tenants) || tenants.isEmpty()) {
            // 如果返回的数据为空或空列表，直接返回空列表
            log.warn("No tenants found for CERTIFICATION_SERVICE_NUMBER");
            return new ArrayList<>();
        }

        // 过滤状态为正常的项目
        return tenants.stream()
                .filter(tenant -> TenantStatusEnum.NORMAL.equals(tenant.getStatus()))
                .collect(Collectors.toList());
    }
}
