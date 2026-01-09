package com.caring.sass.wx.dao.config;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.wx.entity.config.AppComponentToken;
import org.springframework.stereotype.Repository;

/**
 * @ClassName AppComponentToken
 * @Description
 * @Author yangShuai
 * @Date 2021/12/29 16:39
 * @Version 1.0
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface AppComponentTokenMapper extends SuperMapper<AppComponentToken> {
}
