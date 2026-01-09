package com.caring.sass.wx.dao.config;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;

import com.caring.sass.wx.entity.config.WechatOrders;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 微信支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-20
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface WechatOrdersMapper extends SuperMapper<WechatOrders> {

}
