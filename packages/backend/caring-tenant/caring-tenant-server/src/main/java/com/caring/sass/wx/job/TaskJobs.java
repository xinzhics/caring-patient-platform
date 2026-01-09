package com.caring.sass.wx.job;

import com.caring.sass.tenant.service.StatisticsTenantService;
import com.caring.sass.tenant.service.TenantOperateService;
import com.caring.sass.tenant.service.TenantService;
import com.caring.sass.tenant.service.router.H5CoreFunctionsService;
import com.caring.sass.tenant.service.router.H5RouterService;
import com.caring.sass.utils.SpringUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 任务调动中心配置cron规则 {@see http://39.106.72.188:8767/xxl-job-admin/jobinfo}
 *
 * @author xinzh
 */
@Slf4j
@Component
public class TaskJobs {

    private final StatisticsTenantService statisticsTenantService;

    private final H5RouterService h5RouterService;

    private final TenantService tenantService;

    private final TenantOperateService tenantOperateService;

    private final H5CoreFunctionsService h5CoreFunctionsService;

    public TaskJobs(StatisticsTenantService statisticsTenantService,
                    TenantService tenantService,
                    TenantOperateService tenantOperateService,
                    H5CoreFunctionsService h5CoreFunctionsService,
                    H5RouterService h5RouterService) {
        this.statisticsTenantService = statisticsTenantService;
        this.h5RouterService = h5RouterService;
        this.tenantService = tenantService;
        this.tenantOperateService = tenantOperateService;
        this.h5CoreFunctionsService = h5CoreFunctionsService;
    }

    /**
     * syncNursingStaffFromDb 每天零点同步统计信息
     */
    @XxlJob("syncStatisticsTenantFromDb")
    public ReturnT<String> syncStatisticsTenantFromDb(String param) {
        log.error("[syncStatisticsTenantFromDb] 定时任务开始 执行");
        statisticsTenantService.syncFromDb();
        return ReturnT.SUCCESS;
    }

    /**
     * 减少项目的剩余时长
     * @param param
     * @return
     */
    @XxlJob("tenantOperationalEvents")
    public ReturnT<String> tenantOperationalEvents(String param) {
        log.error("[tenantOperationalEvents] 定时任务开始 执行");
        tenantOperateService.tenantOperationalEvents();
        return ReturnT.SUCCESS;
    }

    /**
     * 初始化患者个人中心的核心功能菜单
     * @param param
     * @return
     */
    @XxlJob("initH5CoreFunction")
    public ReturnT<String> initH5CoreFunction(String param) {
        log.error("[initH5CoreFunction] 定时任务开始 执行");
        h5CoreFunctionsService.initH5CoreFunction();
        return ReturnT.SUCCESS;
    }

    @XxlJob("initNursingAppMenu")
    public ReturnT<String> initNursingAppMenu(String param) {
        log.error("[initNursingAppMenu] 定时任务开始 执行");
        h5RouterService.initNursingAppMenu();
        return ReturnT.SUCCESS;
    }

    @XxlJob("initFollowUpMenu")
    public ReturnT<String> initFollowUpMenu(String param) {
        log.error("[initFollowUpMenu] 定时任务开始 执行");
        h5RouterService.initFollowUpMenu();
        return ReturnT.SUCCESS;
    }

    @XxlJob("initTenantDoctorQrCode")
    public ReturnT<String> initTenantDoctorQrCode(String param) {
        log.error("[initTenantDoctorQrCode] 定时任务开始 执行");
//        tenantService.initTenantDoctorQrCode();
        return ReturnT.SUCCESS;
    }

    /**
     * 初始化医助的激活码
     * @param param
     * @return
     */
    @XxlJob("initTenantAssistantQrCode")
    public ReturnT<String> initTenantAssistantQrCode(String param) {
        log.error("[initTenantAssistantQrCode] 定时任务开始 执行");
        tenantService.initTenantAssistantQrCode();
        return ReturnT.SUCCESS;
    }

    /**
     * 手动执行。 执行前需要清理空 c_common_dictionary_item 表
     * @param param
     * @return
     */
    @XxlJob("initTenantDict")
    public ReturnT<String> initTenantDict(String param) {
        log.error("[initTenantDict] 定时任务开始 执行");
        TenantService bean = SpringUtils.getBean(TenantService.class);
        bean.initTenantDict();
        return ReturnT.SUCCESS;
    }

    /**
     * 服务启动时，
     * 查询之前没有删除完毕的 项目。 继续执行删除
     */
    @XxlJob("initDeletingTenant")
    public ReturnT<String> initDeletingTenant(String param) {
        log.error("[initDeletingTenant] 定时任务开始 执行");
        tenantService.queryDeletingTenant();
        return ReturnT.SUCCESS;
    }


}
