package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;

import com.caring.sass.user.entity.KeywordSetting;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 关键字设置
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Repository
public interface KeywordSettingMapper extends SuperMapper<KeywordSetting> {

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<KeywordSetting> selectAllTenantCode();

}
