package com.caring.sass.ai.textual.service;

import com.caring.sass.ai.entity.textual.TextualConsumptionType;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.base.service.SuperService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 业务接口
 * 文献解读用户表
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
public interface TextualInterpretationUserService extends SuperService<TextualInterpretationUser> {

    /**
     * 扣除用户的能量豆
     * @param userId
     * @param i
     * @return
     */
    @Transactional
    boolean deductEnergy(Long userId, int i, TextualConsumptionType desc);


    /**
     * 获取微信授权链接
     * @param redirectUri
     * @return
     */
    String redirectWxAuthUrl(String redirectUri);
}
