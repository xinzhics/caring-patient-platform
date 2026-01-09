package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.redis.KeywordProjectSettingsRedisDTO;
import com.caring.sass.user.entity.KeywordProjectSettings;

/**
 * <p>
 * 业务接口
 * 项目关键字开关配置
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
public interface KeywordProjectSettingsService extends SuperService<KeywordProjectSettings> {

    KeywordProjectSettingsRedisDTO getKeywordProjectSetting();
}
