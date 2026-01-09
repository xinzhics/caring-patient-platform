package com.caring.sass.sms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.sms.entity.SmsTemplate;

/**
 * <p>
 * 业务接口
 * 短信模板
 * </p>
 *
 * @author caring
 * @date 2019-08-01
 */
public interface SmsTemplateService extends SuperService<SmsTemplate> {
    /**
     * 保存模板，并且将模板内容解析成json格式
     *
     * @param smsTemplate
     * @return
     * @author caring
     * @date 2019-05-16 21:13
     */
    void saveTemplate(SmsTemplate smsTemplate);

    /**
     * 修改
     *
     * @param smsTemplate
     */
    void updateTemplate(SmsTemplate smsTemplate);
}
