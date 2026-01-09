package com.caring.sass.user.service.impl;



import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.user.dao.KeywordTriggerRecordMapper;
import com.caring.sass.user.entity.KeywordTriggerRecord;
import com.caring.sass.user.service.KeywordTriggerRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 关键字触发日期
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Service

public class KeywordTriggerRecordServiceImpl extends SuperServiceImpl<KeywordTriggerRecordMapper, KeywordTriggerRecord> implements KeywordTriggerRecordService {
}
