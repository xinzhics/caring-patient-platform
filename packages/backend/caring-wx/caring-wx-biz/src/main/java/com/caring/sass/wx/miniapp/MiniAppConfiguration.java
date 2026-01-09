package com.caring.sass.wx.miniapp;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.hutool.core.collection.CollUtil;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.service.miniApp.MiniAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信配置
 *
 * @author leizhi
 */
@Slf4j
@Configuration
public class MiniAppConfiguration {

    @Bean
    public WxMaService wxMaService() {
        return new WxMaServiceImpl();
    }


}
