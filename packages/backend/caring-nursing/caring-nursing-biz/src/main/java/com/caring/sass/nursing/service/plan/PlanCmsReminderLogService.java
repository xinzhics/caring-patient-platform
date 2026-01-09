package com.caring.sass.nursing.service.plan;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.plan.PlanCmsReminderLog;

/**
 * @ClassName PlanCmsReminderLogMapper
 * @Description
 * @Author yangShuai
 * @Date 2021/12/20 11:14
 * @Version 1.0
 */
public interface PlanCmsReminderLogService extends SuperService<PlanCmsReminderLog> {


    void submitSyncSave(PlanCmsReminderLog planCmsReminderLog, String tenantCode);


    void submitSyncCmsLinkSave(PlanCmsReminderLog planCmsReminderLog, String tenantCode);


}
