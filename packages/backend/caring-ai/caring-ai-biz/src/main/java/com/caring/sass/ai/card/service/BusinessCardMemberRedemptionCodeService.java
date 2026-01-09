package com.caring.sass.ai.card.service;

import com.caring.sass.ai.dto.card.BusinessCardMemberRedemptionCodeSaveDTO;
import com.caring.sass.ai.entity.card.BusinessCardMemberRedemptionCode;
import com.caring.sass.base.service.SuperService;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

/**
 * <p>
 * 业务接口
 * 机构会员兑换码
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
public interface BusinessCardMemberRedemptionCodeService extends SuperService<BusinessCardMemberRedemptionCode> {

    Boolean createRedemptionCode(BusinessCardMemberRedemptionCodeSaveDTO businessCardMemberRedemptionCodeSaveDTO);

}
