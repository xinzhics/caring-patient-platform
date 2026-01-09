package com.caring.sass.wx.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.service.config.ConfigService;
import com.caring.sass.wx.service.handler.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
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
public class WxMpConfiguration {

    private final ConfigService configService;

    private final TextHandler textHandler;
    private final SubscribeHandler subscribeHandler;
    private final UnSubscribeHandler unSubscribeHandler;
    private final ImageMessageHandler imageMessageHandler;
    private final VideoMessageHandler videoMessageHandler;
    private final VoiceMessageHandler voiceMessageHandler;
    private final LocationMessageHandler locationMessageHandler;
    private final ScanMessageHandler scanMessageHandler;
    private final ClickMessageHandler clickMessageHandler;
    private final MassMessageHandler massMessageHandler;

    public WxMpConfiguration(ConfigService configService, TextHandler textHandler,
                             SubscribeHandler subscribeHandler, UnSubscribeHandler unSubscribeHandler,
                             ImageMessageHandler imageMessageHandler, VideoMessageHandler videoMessageHandler,
                             VoiceMessageHandler voiceMessageHandler, LocationMessageHandler locationMessageHandler,
                             ScanMessageHandler scanMessageHandler, ClickMessageHandler clickMessageHandler,
                             MassMessageHandler massMessageHandler) {
        this.configService = configService;
        this.textHandler = textHandler;
        this.subscribeHandler = subscribeHandler;
        this.unSubscribeHandler = unSubscribeHandler;
        this.imageMessageHandler = imageMessageHandler;
        this.videoMessageHandler = videoMessageHandler;
        this.voiceMessageHandler = voiceMessageHandler;
        this.locationMessageHandler = locationMessageHandler;
        this.scanMessageHandler = scanMessageHandler;
        this.clickMessageHandler = clickMessageHandler;
        this.massMessageHandler = massMessageHandler;
    }

    @Bean
    public WxMpService wxMpService() {
        WxMpService service = new WxMpServiceImpl();
        List<Config> configs = configService.listAllConfigWithoutTenant();
        if (CollUtil.isEmpty(configs)) {
            return service;
        }
        service.setMultiConfigStorages(configs
                .stream().map(a -> {
                    SaasWxDefaultConfigImpl configStorage = new SaasWxDefaultConfigImpl();
                    configStorage.setAppId(a.getAppId());
                    if (a.getThirdAuthorization() != null && a.getThirdAuthorization() && StringUtils.isNotEmptyString(a.getAuthorizerAccessToken())) {
                        configStorage.updateAccessToken(a.getAuthorizerAccessToken(), 7200);
                        configStorage.setThirdAuthorization(true);
                    } else {
                        configStorage.setSecret(a.getAppSecret());
                        configStorage.setToken(a.getToken());
                        configStorage.setAesKey(a.getAseKey());
                        configStorage.setThirdAuthorization(false);
                    }
                    return configStorage;
                }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));
        return service;
    }

    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter wxMpMessageRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        wxMpMessageRouter.rule().handler((wxMessage, context, wxMpService1, sessionManager) -> {
            log.info("\n接收到请求消息，内容：{}", JSONUtil.toJsonStr(wxMessage));
            return null;
        }).next();

        // 不能异步，异步会丢失租户信息
        wxMpMessageRouter.rule().async(false).msgType(WxConsts.XmlMsgType.TEXT).handler(textHandler).end()
                .rule().async(false).msgType(WxConsts.XmlMsgType.IMAGE).handler(imageMessageHandler).end()
                .rule().async(false).msgType(WxConsts.XmlMsgType.VIDEO).handler(videoMessageHandler).end()
                .rule().async(false).msgType(WxConsts.XmlMsgType.VOICE).handler(voiceMessageHandler).end()
                .rule().async(false).msgType(WxConsts.XmlMsgType.LOCATION).handler(locationMessageHandler).end()
                // 公众号关注
                .rule().async(false).event(WxConsts.EventType.SUBSCRIBE).handler(subscribeHandler).end()
                // 取消关注公众
                .rule().async(false).event(WxConsts.EventType.UNSUBSCRIBE).handler(unSubscribeHandler).end()
                .rule().async(false).event(WxConsts.EventType.MASS_SEND_JOB_FINISH).handler(massMessageHandler).end()
                .rule().async(false).event(WxConsts.EventType.SCAN).handler(scanMessageHandler).end()
                .rule().async(false).event(WxConsts.EventType.CLICK).handler(clickMessageHandler).end();
        return wxMpMessageRouter;
    }

}
