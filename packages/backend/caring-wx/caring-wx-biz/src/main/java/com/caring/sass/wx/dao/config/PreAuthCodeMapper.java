package com.caring.sass.wx.dao.config;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.wx.entity.config.PreAuthCode;
import org.springframework.stereotype.Repository;

/**
 * @ClassName PreAuthCodeMapper
 * @Description
 * @Author yangShuai
 * @Date 2021/12/31 9:48
 * @Version 1.0
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface PreAuthCodeMapper extends SuperMapper<PreAuthCode> {
}
