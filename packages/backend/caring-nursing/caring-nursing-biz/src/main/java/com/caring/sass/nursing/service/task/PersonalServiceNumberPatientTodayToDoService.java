package com.caring.sass.nursing.service.task;


import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.nursing.dto.follow.PatientMenuFollowItem;
import com.caring.sass.nursing.service.plan.PatientNursingPlanService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantDiseasesTypeEnum;
import com.caring.sass.user.dto.PatientPageDTO;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 个人服务号 患者今日待办 查询并推送提醒
 */
@Slf4j
@Service
public class PersonalServiceNumberPatientTodayToDoService {


    @Autowired
    RedisTemplate<String, String> redisTemplate;


    @Autowired
    PatientApi patientApi;


    @Autowired
    BusinessReminderLogControllerApi reminderLogControllerApi;

    @Autowired
    TenantApi tenantApi;

    private static ExecutorService executor;
    private final AtomicInteger maximumPoolSize = new AtomicInteger(0);

    public PersonalServiceNumberPatientTodayToDoService() {
        NamedThreadFactory threadFactory = new NamedThreadFactory("personal_service_number_patient_todd_", false);
        executor = new ThreadPoolExecutor(0, 2, 0L,
                TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(500),
                threadFactory);
    }

    /**
     * 检查任务线程 是否有空闲
     */
    public void checkPoolSize() {
        while (maximumPoolSize.get() >= 3) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
    }


    /**
     * 控制本地线程数
     */
    public void getRunning() {

        // 先判断当前是是否有 可用的线程。
        checkPoolSize();
    }


    /**
     * 每天早上7点。开始从redis中pop任务出来。进行执行。
     */
    public void queryAndCreatePushTask() {

        while (true) {
            String tenantCode = redisTemplate.opsForList().rightPop("PersonalServiceNumberPatientTodayToDo");
            if (StrUtil.isEmpty(tenantCode)) {
                break;
            }
            getRunning();
            // 开始执行本项目的任务
            try {
                maximumPoolSize.incrementAndGet();
                executor.execute(() -> processTenantTasks(tenantCode));
            } catch (Exception e) {
                log.error("Processing tasks for tenantCode: {} failed", tenantCode, e);
            }
        }


    }


    /**
     * 处理一个项目的 患者今日待办。对齐进行发送短信
     * https://kailing.domain/wx/home
     * @param tenantCode
     */
    public void processTenantTasks(String tenantCode) {
        try {
            BaseContextHandler.setTenant(tenantCode);
            PageParams<PatientPageDTO> params = new PageParams<>();
            PatientPageDTO pageDTO = new PatientPageDTO();
            pageDTO.setTenantCode(tenantCode);
            R<Tenant> tenantR = tenantApi.getByCode(tenantCode);
            Tenant tenant = tenantR.getData();
            TenantDiseasesTypeEnum diseasesType = TenantDiseasesTypeEnum.other;
            if (Objects.nonNull(tenant) && Objects.nonNull(tenant.getDiseasesType())) {
                diseasesType = tenant.getDiseasesType();
            }
            pageDTO.setIsCompleteEnterGroup(CommonStatus.YES);
            params.setModel(pageDTO);
            int current = 1;
            PatientNursingPlanService patientNursingPlanService = SpringUtils.getBean(PatientNursingPlanService.class);
            String wxPatientBizUrl = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), "home");
            while (true) {

                params.setCurrent(current);
                params.setSize(50);
                R<IPage<Patient>> exportPageWithScope = patientApi.exportPageWithScope(params);
                IPage<Patient> scopeData = exportPageWithScope.getData();

                // 根据查询到的患者。去查询患者今日待办
                List<Patient> records = scopeData.getRecords();
                if (records.isEmpty()) {
                    return;
                }
                for (Patient record : records) {
                    List<PatientMenuFollowItem> menuFollowItems = patientNursingPlanService.patientMenuFollow(record.getId(), false);
                    if (menuFollowItems.isEmpty()) {
                        continue;
                    }
                    List<String> taskNameList = new ArrayList<>();
                    for (PatientMenuFollowItem followItem : menuFollowItems) {
                        if (!taskNameList.contains(followItem.getName())) {
                            taskNameList.add(followItem.getName());
                        }
                    }

                    String smsParams = BusinessReminderType.getPatientTodayToDoListSmsParams(tenant.getName(),
                            menuFollowItems.size(),
                            taskNameList);

                    // 创建一条 今日待办消息 的推送任务
                    BusinessReminderLogSaveDTO logSaveDTO = BusinessReminderLogSaveDTO.builder()
                            .mobile(record.getMobile())
                            .wechatRedirectUrl(wxPatientBizUrl)
                            .diseasesType(diseasesType.toString())
                            .type(BusinessReminderType.PATIENT_TODAY_TO_DO_LIST)
                            .tenantCode(tenantCode)
                            .queryParams(smsParams)
                            .patientId(record.getId())
                            .doctorId(record.getDoctorId())
                            .nursingId(record.getServiceAdvisorId())
                            .status(0)
                            .openThisMessage(0)
                            .finishThisCheckIn(0)
                            .build();

                    R<Boolean> booleanR = reminderLogControllerApi.sendNoticeSms(logSaveDTO);
                    System.out.println(booleanR.getData());
                }
                current++;
                if (current > scopeData.getPages()) {
                    break;
                }
            }

        } finally {
            maximumPoolSize.decrementAndGet();
        }
    }
}
