package com.caring.sass.cms.service.impl;


import cn.hutool.core.util.StrUtil;
import com.caring.sass.cms.dao.BannerSwitchMapper;
import com.caring.sass.cms.entity.BannerSwitch;
import com.caring.sass.cms.service.BannerSwitchService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * banner
 * </p>
 *
 * @author 杨帅
 * @date 2023-12-08
 */
@Slf4j
@Service

public class BannerSwitchServiceImpl extends SuperServiceImpl<BannerSwitchMapper, BannerSwitch> implements BannerSwitchService {


    @Override
    public boolean save(BannerSwitch model) {
        String tenantCode = model.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        BannerSwitch bannerSwitch = baseMapper.selectOne(Wraps.<BannerSwitch>lbQ().eq(BannerSwitch::getUserRole, model.getUserRole()));
        if (Objects.nonNull(bannerSwitch)) {
            model.setId(bannerSwitch.getId());
            super.updateById(model);
        } else {
            return super.save(model);
        }
        return true;
    }
}
