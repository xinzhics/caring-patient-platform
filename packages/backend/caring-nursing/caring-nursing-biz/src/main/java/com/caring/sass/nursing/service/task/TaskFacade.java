package com.caring.sass.nursing.service.task;

import com.caring.sass.cms.MassCallBackApi;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.task.impl.*;
import com.caring.sass.oauth.api.PatientApi;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author xinzh
 */
@Component
public class TaskFacade {

    private final PushDrugsTask pushDrugsTask;

    private final PushBuyDrugsTask pushBuyDrugsTask;

    private final StopPatientDrugsTask stopPatientDrugsTask;

    private final NursingPlanTask planTaskService;

    private final MassCallBackApi massCallBackApi;

    private final PatientApi patientApi;

    public TaskFacade(
                      PushDrugsTask pushDrugsTask,
                      PushBuyDrugsTask pushBuyDrugsTask, StopPatientDrugsTask stopPatientDrugsTask,
                      NursingPlanTask planTaskService,
                      PatientApi patientApi,
                      MassCallBackApi massCallBackApi) {
        this.pushDrugsTask = pushDrugsTask;
        this.pushBuyDrugsTask = pushBuyDrugsTask;
        this.stopPatientDrugsTask = stopPatientDrugsTask;
        this.planTaskService = planTaskService;
        this.massCallBackApi = massCallBackApi;
        this.patientApi = patientApi;
    }

    /**
     * 给患者推送吃药提醒
     * 已分发
     * @param dateTime
     */
    public void pushDrugs(LocalDateTime dateTime) {
        pushDrugsTask.execute(dateTime);
    }

    /**
     * 对已经需要停止的用药提醒，设置定制
     */
    public void stopPatientDrugsTask() {
        stopPatientDrugsTask.execute();
    }

    /**
     * 血压 血糖 健康日志 复查提醒的 推送任务分发
     * @param planEnum
     * @param localDateTime
     */
    public void nursingPlanPush(PlanEnum planEnum, LocalDateTime localDateTime) {
        planTaskService.execute(planEnum, localDateTime);
    }

    /**
     * 处理学习计划的推送
     * 已分发
     * @param localDateTime
     */
    public void learnPlanPush(LocalDateTime localDateTime) {
        planTaskService.learnPlanExecute(localDateTime);
    }

    /**
     * 处理 指标监测 或者 自定义随访
     * 已分发
     * @param localDateTime
     */
    public void customPlanPush(LocalDateTime localDateTime) {
        planTaskService.customPlanPushExecute(localDateTime);
    }

    /**
     * 患者自定义的提醒时间
     * 已处理
     */
    public void patientCustomPlan(LocalDateTime dateTime) {
        planTaskService.customNursingPlanExecute(dateTime);
    }


    /**
     * 患者注射超时
     * 已处理
     * @param dateTime
     */
    public void injectionFormTimeOutRemind(LocalDateTime dateTime) {
        planTaskService.injectionFormTimeOutRemind(dateTime);
    }

    /**
     * 检查是否有要推送的记录。
     *
     * 推送前要检查一下计划是否还在开启状态
     *
     * @param localDateTime
     */
    public void checkReminderLog(LocalDateTime localDateTime) {
        planTaskService.checkReminderLog(localDateTime);
    }

    /**
     * 调用cms服务。 检测是否有群发图文待发送
     */
    public void executeCmsMassMailing() {
        massCallBackApi.massMailing();
    }

    public void unregisteredReminder() {
        patientApi.unregisteredReminder();
    }

    /**
     * 推送购药提醒
     */
    public void pushBuyDrugsTask(LocalDateTime localDateTime) {
        pushBuyDrugsTask.execute(localDateTime);
    }

    /**
     * 处理redis队列中的推送任务
     */
    public void handleMessage() {

        planTaskService.handleMessage();
    }

    /**
     * 处理redis中的用药信息
     */
    public void handleDrugsMessage() {

        pushDrugsTask.handleMessage();

    }
}
