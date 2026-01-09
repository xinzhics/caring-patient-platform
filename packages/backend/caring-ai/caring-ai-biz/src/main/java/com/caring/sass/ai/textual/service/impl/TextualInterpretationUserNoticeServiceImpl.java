package com.caring.sass.ai.textual.service.impl;


import com.caring.sass.ai.entity.textual.TextualInterpretationUserNotice;
import com.caring.sass.ai.textual.dao.TextualInterpretationUserNoticeMapper;
import com.caring.sass.ai.textual.service.TextualInterpretationUserNoticeService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 文献解读系统通知
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Service

public class TextualInterpretationUserNoticeServiceImpl extends SuperServiceImpl<TextualInterpretationUserNoticeMapper, TextualInterpretationUserNotice> implements TextualInterpretationUserNoticeService {
}
