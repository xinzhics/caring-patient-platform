package com.caring.sass.nursing.controller.plan;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.dto.plan.ReminderLogPageDTO;
import com.caring.sass.nursing.dto.plan.ReminderLogSaveDTO;
import com.caring.sass.nursing.dto.plan.ReminderLogUpdateDTO;
import com.caring.sass.nursing.entity.plan.ReminderLog;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.plan.ReminderLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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
@RequestMapping("/reminderLog")
@Api(value = "PlanReminderLogController", tags = "推送消息记录")
public class PlanReminderLogController extends SuperController<ReminderLogService, Long, ReminderLog, ReminderLogPageDTO, ReminderLogSaveDTO, ReminderLogUpdateDTO> {


    @ApiOperation("微信推送完模板消息后回调")
    @GetMapping("weixinMessageCallback")
    public void weixinMessageCallback(@RequestParam("messageId") Long messageId,
                                         @RequestParam("status") Integer status,
                                         @RequestParam("tenantCode") String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        baseService.update(Wraps.<ReminderLog>lbU()
                .set(ReminderLog::getStatus, status)
                .eq(ReminderLog::getStatus, -2)
                .eq(SuperEntity::getId, messageId));
    }

    @ApiOperation("用户打开了一个消息")
    @GetMapping("openMessage")
    public R<Void> openMessage(@RequestParam("messageId") Long messageId) {

        baseService.openMessage(messageId);
        return R.success(null);
    }

    @ApiOperation("无授权用户打开了文章")
    @GetMapping("anno/submitSuccess")
    public R<Void> annoSubmitSuccess(@RequestParam("messageId") Long messageId) {

        baseService.submitSuccess(messageId);
        return R.success(null);
    }

    @ApiOperation("用户打开了文章")
    @GetMapping("submitSuccess")
    public R<Void> submitSuccess(@RequestParam("messageId") Long messageId) {

        baseService.submitSuccess(messageId);
        return R.success(null);
    }

    @ApiOperation("患者待办中直接打开文章")
    @GetMapping("submitCms")
    public R<Void> submitCms(@RequestParam(value = "messageId", required = false) Long messageId,
                                 @RequestParam() Long patientId,
                                 @RequestParam(value = "planDetailTimeId", required = false) Long planDetailTimeId) {

        if (Objects.nonNull(messageId)) {
            baseService.submitSuccess(messageId);
        } else if (Objects.nonNull(planDetailTimeId)) {
            baseService.createOrUpdateReminderLog(planDetailTimeId, PlanEnum.LEARN_PLAN, patientId, null);
        } else {
            throw new BizException("参数错误");
        }
        return R.success(null);
    }

    @ApiOperation("用户打开了外部链接完成打卡")
    @GetMapping("anno/externalMessages")
    public R<Void> externalMessages(@RequestParam("messageId") Long messageId, @RequestParam("tenantCode") String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        baseService.externalMessages(messageId);
        return R.success(null);
    }


}
