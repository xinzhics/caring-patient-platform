package com.caring.sass.msgs.service.impl;


import com.caring.sass.msgs.dao.PatientSystemMessageRemarkMapper;
import com.caring.sass.msgs.entity.PatientSystemMessageRemark;
import com.caring.sass.msgs.service.PatientSystemMessageRemarkService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 患者系统消息备注表
 * </p>
 *
 * @author 杨帅
 * @date 2024-12-26
 */
@Slf4j
@Service

public class PatientSystemMessageRemarkServiceImpl extends SuperServiceImpl<PatientSystemMessageRemarkMapper, PatientSystemMessageRemark> implements PatientSystemMessageRemarkService {
}
