package com.caring.sass.ai.call.service.impl;


import com.caring.sass.ai.call.dao.CallConfigMapper;
import com.caring.sass.ai.call.service.CallConfigService;
import com.caring.sass.ai.entity.call.CallConfig;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 通话充值配置
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
@Slf4j
@Service
public class CallConfigServiceImpl extends SuperServiceImpl<CallConfigMapper, CallConfig> implements CallConfigService {
}
