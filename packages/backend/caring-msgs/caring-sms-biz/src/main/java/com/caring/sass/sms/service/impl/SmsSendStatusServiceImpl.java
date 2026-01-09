package com.caring.sass.sms.service.impl;


import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.sms.dao.SmsSendStatusMapper;
import com.caring.sass.sms.entity.SmsSendStatus;
import com.caring.sass.sms.service.SmsSendStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 短信发送状态
 * </p>
 *
 * @author caring
 * @date 2019-08-01
 */
@Slf4j
@Service

public class SmsSendStatusServiceImpl extends SuperServiceImpl<SmsSendStatusMapper, SmsSendStatus> implements SmsSendStatusService {

}
