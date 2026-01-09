package com.caring.sass.ai.ckd.server.impl;


import com.caring.sass.ai.ckd.dao.CkdUserInfoMapper;
import com.caring.sass.ai.ckd.dao.CkdUserShareMapper;
import com.caring.sass.ai.ckd.server.CkdUserShareService;
import com.caring.sass.ai.dto.ckd.CkdMembershipLevel;
import com.caring.sass.ai.entity.ckd.CkdUserInfo;
import com.caring.sass.ai.entity.ckd.CkdUserShare;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * ckd会员分享成功转换记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-14
 */
@Slf4j
@Service

public class CkdUserShareServiceImpl extends SuperServiceImpl<CkdUserShareMapper, CkdUserShare> implements CkdUserShareService {

    @Autowired
    CkdUserInfoMapper ckdUserInfoMapper;


    @Transactional
    @Override
    public boolean save(CkdUserShare model) {

        Long shareUserId = model.getShareUserId();
        CkdUserInfo userInfo = ckdUserInfoMapper.selectOne(Wraps.<CkdUserInfo>lbQ()
                .eq(SuperEntity::getCreateUser, shareUserId)
                .last(" limit 0,1 "));
        if (Objects.nonNull(userInfo)) {
            Long fanUsers = model.getFanUsers();
            CkdUserInfo fanUsersCkdInfo = ckdUserInfoMapper.selectOne(Wraps.<CkdUserInfo>lbQ()
                    .eq(SuperEntity::getCreateUser, fanUsers)
                    .last(" limit 0,1 "));
            Integer freeDurationFans = userInfo.getFreeDurationFans();
            if (freeDurationFans == null) {
                freeDurationFans = 7;
            }
            fanUsersCkdInfo.setFreeDurationFans(freeDurationFans);
            if (fanUsersCkdInfo.getMembershipLevel().equals(CkdMembershipLevel.FREE)) {
                if (!freeDurationFans.equals(7)) {
                    fanUsersCkdInfo.setExpirationDate(fanUsersCkdInfo.getExpirationDate().plusDays(freeDurationFans -7));
                }
            }
            ckdUserInfoMapper.updateById(fanUsersCkdInfo);
        }
        return super.save(model);
    }
}
