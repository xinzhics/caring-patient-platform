package com.caring.sass.sms.controller;

import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.entity.BusinessReminderLog;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import com.caring.sass.sms.service.BusinessReminderLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-17
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessReminderLog")
@Api(value = "BusinessReminderLog", tags = "业务短信通知")
public class BusinessReminderLogController {

    @Autowired
    BusinessReminderLogService businessReminderLogService;

    /**
     * 提供 租户， 手机号， 短信模版code， 短信参数
     * @return
     */
    @ApiOperation("保存短信并发送")
    @PostMapping("/send")
    public R<Boolean> sendNoticeSms(@RequestBody BusinessReminderLogSaveDTO businessReminderLogSaveDTO) {

        businessReminderLogService.createBusinessReminderLog(businessReminderLogSaveDTO);
        return R.success(true);

    }


    /**
     * 根据短链接参数，查询短信业务。
     * 根据业务进行功能重定向跳转
     */
    @ApiOperation("查询短信业务内容后重定向")
    @GetMapping("/anno/querySmsContent")
    public void getWxUserCode(HttpServletRequest req, HttpServletResponse resp, @RequestParam(value = "smsId") String smsId) {

        BusinessReminderLog reminderLog = businessReminderLogService.getOne(Wraps.<BusinessReminderLog>lbQ()
                .eq(BusinessReminderLog::getSmsParamId, smsId)
                .eq(BusinessReminderLog::getStatus, 1));
        if (Objects.isNull(reminderLog)) {
            // 重定向到短信已失效页面
            return;
        }
        String wechatRedirectUrl = reminderLog.getWechatRedirectUrl();
        reminderLog.setOpenThisMessage(1);
        reminderLog.setFinishThisCheckIn(1);
        businessReminderLogService.updateById(reminderLog);
        try {
            resp.sendRedirect(wechatRedirectUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    }
