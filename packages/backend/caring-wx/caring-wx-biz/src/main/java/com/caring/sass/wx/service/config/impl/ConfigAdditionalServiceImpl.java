package com.caring.sass.wx.service.config.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.wx.dao.config.ConfigAdditionalMapper;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.service.config.ConfigAdditionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 微信附加信息配置
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class ConfigAdditionalServiceImpl extends SuperServiceImpl<ConfigAdditionalMapper, ConfigAdditional> implements ConfigAdditionalService {


    @Override
    public ConfigAdditional getConfigAdditionalByWxAppId(String appId) {
        return baseMapper.selectOne(Wrappers.lambdaQuery());
    }

    @Override
    public ConfigAdditional getConfigAdditionalByProjectId() {
        return baseMapper.selectOne(Wrappers.lambdaQuery());
    }


    @Override
    public Integer getKeyWordStatus() {
        ConfigAdditional selectOne = baseMapper.selectOne(Wrappers.lambdaQuery());
        return selectOne.getKeyWordStatus();
    }


    @Override
    public Integer updateKeyWordStatus(Integer keyWordStatus) {
        ConfigAdditional selectOne = baseMapper.selectOne(Wrappers.lambdaQuery());
        if (Objects.nonNull(selectOne)) {
            selectOne.setKeyWordStatus(keyWordStatus);
            baseMapper.updateById(selectOne);
        } else {
            selectOne = new ConfigAdditional();
            selectOne.setKeyWordStatus(keyWordStatus);
            baseMapper.insert(selectOne);
        }

        return keyWordStatus;
    }


    @Override
    public Integer getAutomaticReply() {
        ConfigAdditional selectOne = baseMapper.selectOne(Wrappers.lambdaQuery());
        return selectOne.getAutomaticReply();
    }

    @Override
    public Integer updateAutomaticReply(Integer automaticReply) {
        ConfigAdditional selectOne = baseMapper.selectOne(Wrappers.lambdaQuery());
        if (selectOne != null) {
            selectOne.setAutomaticReply(automaticReply);
            baseMapper.updateById(selectOne);
        } else {
            selectOne = new ConfigAdditional();
            selectOne.setAutomaticReply(automaticReply);
            baseMapper.insert(selectOne);
        }
        return automaticReply;
    }
}
