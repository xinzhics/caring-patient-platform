package com.caring.sass.ai.card.service.impl;



import com.caring.sass.ai.card.dao.BusinessCardMemberRedemptionCodeMapper;
import com.caring.sass.ai.card.service.BusinessCardMemberRedemptionCodeService;
import com.caring.sass.ai.dto.card.BusinessCardMemberRedemptionCodeSaveDTO;
import com.caring.sass.ai.entity.card.BusinessCardMemberRedemptionCode;
import com.caring.sass.ai.entity.card.BusinessCardMemberVersionEnum;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 业务实现类
 * 机构会员兑换码
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
@Slf4j
@Service

public class BusinessCardMemberRedemptionCodeServiceImpl extends SuperServiceImpl<BusinessCardMemberRedemptionCodeMapper, BusinessCardMemberRedemptionCode> implements BusinessCardMemberRedemptionCodeService {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int STRING_LENGTH = 8;

    public static String generateSecureRandomString() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(STRING_LENGTH);

        for (int i = 0; i < STRING_LENGTH; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }

        return stringBuilder.toString();
    }
    /**
     * 根据机构ID， 和要求的 生成的兑换码数量。 生成对应的 兑换码
     * 兑换码为 8位的数字。
     * 兑换码需要保持唯一性
     * @param businessCardMemberRedemptionCodeSaveDTO
     * @return
     */
    @Override
    public Boolean createRedemptionCode(BusinessCardMemberRedemptionCodeSaveDTO businessCardMemberRedemptionCodeSaveDTO) {

        // 生成数量
        Integer number = businessCardMemberRedemptionCodeSaveDTO.getNumber();

        // 机构的ID
        Long organId = businessCardMemberRedemptionCodeSaveDTO.getOrganId();

        if (number <= 0) {
            return true;
        }
        if (number >= 200) {
            throw new BizException("一次最多创建200个兑换码");
        }

        BusinessCardMemberVersionEnum redemptionCodeVersion = businessCardMemberRedemptionCodeSaveDTO.getRedemptionCodeVersion();

        Set<String> uniqueRedemptionCodes = new HashSet<>();

        while (uniqueRedemptionCodes.size() < number) {
            String redemptionCode = generateSecureRandomString();
            // 检查数据库中是否已经存在该兑换码
            if (!existsByRedemptionCode(redemptionCode)) {
                uniqueRedemptionCodes.add(redemptionCode);
            }
        }

        List<BusinessCardMemberRedemptionCode> businessCardMemberRedemptionCodes = new ArrayList<>(number);
        for (String redemptionCode : uniqueRedemptionCodes) {
            BusinessCardMemberRedemptionCode build = BusinessCardMemberRedemptionCode.builder()
                    .redemptionCode(redemptionCode)
                    .redemptionCodeVersion(redemptionCodeVersion)
                    .deleteFlag(false)
                    .organId(organId)
                    .exchangeStatus(BusinessCardMemberRedemptionCode.EXCHANGE_STATUS_NO_USE)
                    .build();
            businessCardMemberRedemptionCodes.add(build);
        }

        baseMapper.insertBatchSomeColumn(businessCardMemberRedemptionCodes);
        return null;
    }


    /**
     * 检查验证码是否存在
     * @param redemptionCode
     * @return
     */
    public boolean existsByRedemptionCode(String redemptionCode) {
        Integer count = baseMapper.selectCount(Wraps.<BusinessCardMemberRedemptionCode>lbQ().eq(BusinessCardMemberRedemptionCode::getRedemptionCode, redemptionCode));
        if (count != null && count > 0) {
            return true;
        }
        return false;
    }


}
