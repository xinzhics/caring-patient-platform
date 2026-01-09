package com.caring.sass.ai.textual.service;

import com.caring.sass.ai.entity.textual.TextualInterpretationUserVoice;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 文献解读用户声音
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
public interface TextualInterpretationUserVoiceService extends SuperService<TextualInterpretationUserVoice> {

    R<String> voiceClone(Long voiceId, String text);

    void syncCloningVoiceId();
}
