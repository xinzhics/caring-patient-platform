package com.caring.sass.wx.dao.config;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import org.springframework.stereotype.Repository;

/**
 * @ClassName WeiXinUserInfoMapper
 * @Description
 * @Author yangShuai
 * @Date 2022/1/7 16:07
 * @Version 1.0
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface WeiXinUserInfoMapper extends SuperMapper<WeiXinUserInfo> {
}
