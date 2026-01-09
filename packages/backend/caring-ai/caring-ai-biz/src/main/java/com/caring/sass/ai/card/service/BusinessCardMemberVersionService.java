package com.caring.sass.ai.card.service;

import com.caring.sass.ai.entity.card.BusinessCardMemberVersionEnum;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.card.BusinessCardMemberVersion;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 业务接口
 * 用户的会员版本
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
public interface BusinessCardMemberVersionService extends SuperService<BusinessCardMemberVersion> {



    void useCodeExchange(Long userId, String redemptionCode);

    /**
     * 设置用户会员版本
     * @param userId
     */
    @Transactional
    void setBusinessCardUserMemberVersion(Long userId, BusinessCardMemberVersionEnum cardMemberVersionEnum);

    /**
     * 查询用户的会员版本
     * @param userId
     * @return
     */
    BusinessCardMemberVersion queryUserVersion(Long userId);
}
