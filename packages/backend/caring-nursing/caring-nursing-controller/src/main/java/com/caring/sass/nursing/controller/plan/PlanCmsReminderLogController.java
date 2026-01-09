package com.caring.sass.nursing.controller.plan;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.plan.PlanCmsReminderLogPageDTO;
import com.caring.sass.nursing.dto.plan.PlanCmsReminderLogSaveDTO;
import com.caring.sass.nursing.dto.plan.PlanCmsReminderLogUpdateDTO;
import com.caring.sass.nursing.entity.plan.PlanCmsReminderLog;
import com.caring.sass.nursing.service.plan.PlanCmsReminderLogService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName PlanCmsReminderLogController
 * @Description
 * @Author yangShuai
 * @Date 2021/12/20 11:16
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/planCmsReminderLog")
@Api(value = "PlanCmsReminderLogController", tags = "历史推文")
public class PlanCmsReminderLogController extends SuperController<PlanCmsReminderLogService, Long, PlanCmsReminderLog, PlanCmsReminderLogPageDTO, PlanCmsReminderLogSaveDTO, PlanCmsReminderLogUpdateDTO> {
}
