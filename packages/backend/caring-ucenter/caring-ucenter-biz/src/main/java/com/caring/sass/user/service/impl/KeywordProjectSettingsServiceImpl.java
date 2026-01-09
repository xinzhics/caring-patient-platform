package com.caring.sass.user.service.impl;



import com.alibaba.fastjson.JSON;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.redis.UcenterRedisKeyConstant;
import com.caring.sass.user.dao.KeywordProjectSettingsMapper;
import com.caring.sass.user.redis.KeywordProjectSettingsRedisDTO;
import com.caring.sass.user.entity.KeywordProjectSettings;
import com.caring.sass.user.service.KeywordProjectSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 项目关键字开关配置
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Service

public class KeywordProjectSettingsServiceImpl extends SuperServiceImpl<KeywordProjectSettingsMapper, KeywordProjectSettings> implements KeywordProjectSettingsService {


    @Autowired
    RedisTemplate<String, String> redisTemplate;



    @Override
    public boolean save(KeywordProjectSettings model) {

        super.save(model);
        updateRedisSetting(model);
        return true;
    }

    /**
     * 获取项目的关键字回复设置
     * @return
     */
    @Override
    public KeywordProjectSettingsRedisDTO getKeywordProjectSetting() {
        try {
            // 先从redis 上拿
            String tenant = BaseContextHandler.getTenant();
            BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordProjectSettings);
            Object o = operations.get(tenant);
            KeywordProjectSettingsRedisDTO redisDTO;
            if (Objects.nonNull(o)) {
                redisDTO = JSON.parseObject(o.toString(), KeywordProjectSettingsRedisDTO.class);
                if (StringUtils.isNotEmptyString(redisDTO.getKeywordReplySwitch()) && StringUtils.isNotEmptyString(redisDTO.getKeywordReplyForm())) {
                    return redisDTO;
                }
            }
            // 从数据库拿
            KeywordProjectSettings selectOne = baseMapper.selectOne(Wraps.<KeywordProjectSettings>lbQ().last(" limit 0,1 "));
            if (Objects.isNull(selectOne)) {
                selectOne = new KeywordProjectSettings();
                selectOne.setKeywordReplySwitch(KeyWordEnum.close.toString());
                selectOne.setKeywordReplyForm(KeyWordEnum.medical_assistance.toString());
            }
            // 并设置到redis上
            updateRedisSetting(selectOne);
            redisDTO = new KeywordProjectSettingsRedisDTO();
            BeanUtils.copyProperties(selectOne, redisDTO);
            return redisDTO;
        } catch (Exception e) {
            log.info("缓存关键字配置失败");
        }
        return null;
    }



    /**
     * 将项目的关键字回复设置 缓存 到redis中
     * @param model
     * @return
     */
    public void updateRedisSetting(KeywordProjectSettings model) {
        if (StringUtils.isEmpty(model.getKeywordReplySwitch()) || StringUtils.isEmpty(model.getKeywordReplyForm())) {
            return;
        }
        KeywordProjectSettingsRedisDTO redisDTO = new KeywordProjectSettingsRedisDTO();
        BeanUtils.copyProperties(model, redisDTO);
        try {
            String tenant = BaseContextHandler.getTenant();
            BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordProjectSettings);
            operations.put(tenant, JSON.toJSONString(redisDTO));
        } catch (Exception e) {
            log.info("缓存关键字配置失败");
        }
    }


    @Override
    public boolean updateById(KeywordProjectSettings model) {
        KeywordProjectSettings projectSettings = super.getById(model.getId());
        if (StringUtils.isNotEmptyString(model.getKeywordReplyForm())) {
            projectSettings.setKeywordReplyForm(model.getKeywordReplyForm());
        }
        if (StringUtils.isNotEmptyString(model.getKeywordReplySwitch())) {
            projectSettings.setKeywordReplySwitch(model.getKeywordReplySwitch());
        }
        super.updateById(projectSettings);
        updateRedisSetting(projectSettings);
        return true;
    }
}
