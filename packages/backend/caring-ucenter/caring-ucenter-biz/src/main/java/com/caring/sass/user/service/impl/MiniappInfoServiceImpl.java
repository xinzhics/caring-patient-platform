package com.caring.sass.user.service.impl;


import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.user.dao.MiniappInfoMapper;
import com.caring.sass.user.entity.MiniappInfo;
import com.caring.sass.user.service.MiniappInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 小程序用户openId关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-22
 */
@Slf4j
@Service

public class MiniappInfoServiceImpl extends SuperServiceImpl<MiniappInfoMapper, MiniappInfo> implements MiniappInfoService {
    @Override
    public MiniappInfo selectByIdNoTenant(Long id) {
        return baseMapper.selectByIdNoTenant(id);
    }
}
