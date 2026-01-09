package com.caring.sass.sms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import com.caring.sass.sms.entity.BusinessReminderLog;

/**
 * <p>
 * 业务接口
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-17
 */
public interface BusinessReminderLogService extends SuperService<BusinessReminderLog> {


    void createBusinessReminderLog(BusinessReminderLogSaveDTO businessReminderLogSaveDTO);
}
