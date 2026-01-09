package com.caring.sass.nursing.job;

import com.caring.sass.base.R;
import com.caring.sass.nursing.service.task.PersonalServiceNumberPatientTodayToDoService;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 个人服务号的推送任务
 */
@Slf4j
@Component
@AllArgsConstructor
public class PersonalServiceNumberJobs {


    @Autowired
    TenantApi tenantApi;

    @Autowired
    PersonalServiceNumberPatientTodayToDoService personalServiceNumberPatientTodayToDoService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 个人服务号项目，患者今日待办提醒
     * 早上 6点 办 将需要处理的项目 推送近redis
     *
     *
     *
     */
    @XxlJob("PersonalServiceNumberQueryAndCreatePushTask")
    public ReturnT<String> PersonalServiceNumberQueryAndCreatePushTask(String param) {
        log.info("PersonalServiceNumberQueryAndCreatePushTask task start");
        personalServiceNumberPatientTodayToDoService.queryAndCreatePushTask();
        log.info("PersonalServiceNumberQueryAndCreatePushTask task start");
        return ReturnT.SUCCESS;
    }


    /**
     * 个人服务号项目，患者今日待办提醒
     * 早上 6点 办 将需要处理的项目 推送近redis
     *
     *
     *
     */
    @XxlJob("PersonalServiceNumberPatientTodayToDo")
    public ReturnT<String> personalServiceNumberPatientTodayToDo(String param) {
        log.info("personalServiceNumberPatientTodayToDo task start");
        List<Tenant> tenants = getAllNormalTenant();

        for (Tenant tenant : tenants) {

            // 把项目code推进到redis中。然后由两个子任务在7点时，开始不断地领任务进行执行
            redisTemplate.boundListOps("PersonalServiceNumberPatientTodayToDo").leftPush(tenant.getCode());

        }

        log.info("personalServiceNumberPatientTodayToDo task start");
        return ReturnT.SUCCESS;
    }





    /**
     * 获取所有的正常的项目
     */
    protected List<Tenant> getAllNormalTenant() {
        R<List<Tenant>> listR = tenantApi.queryTenantList(TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER);
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
