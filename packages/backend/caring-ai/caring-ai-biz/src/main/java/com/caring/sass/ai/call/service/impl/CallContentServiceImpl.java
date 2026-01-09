package com.caring.sass.ai.call.service.impl;

import com.caring.sass.ai.call.dao.CallContentMapper;
import com.caring.sass.ai.call.service.CallContentService;
import com.caring.sass.ai.entity.call.CallContent;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 分身通话内容
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
@Slf4j
@Service

public class CallContentServiceImpl extends SuperServiceImpl<CallContentMapper, CallContent> implements CallContentService {
}
