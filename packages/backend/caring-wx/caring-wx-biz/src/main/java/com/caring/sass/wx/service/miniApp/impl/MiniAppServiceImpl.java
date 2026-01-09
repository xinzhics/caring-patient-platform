package com.caring.sass.wx.service.miniApp.impl;


import cn.binarywang.wx.miniapp.api.*;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaKefuMessage;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.bean.urllink.GenerateUrlLinkRequest;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.wx.dao.config.ConfigMapper;
import com.caring.sass.wx.dto.config.WxMaKfMessageDto;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.miniapp.MiniAppServiceHolder;
import com.caring.sass.wx.service.config.AppComponentTokenService;
import com.caring.sass.wx.service.miniApp.MiniAppService;
import com.caring.sass.wx.wxUtil.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.subscribemsg.PubTemplateKeyword;
import me.chanjar.weixin.common.bean.subscribemsg.TemplateInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 小程序的业务对接
 *
 * config 中 authType 是 2 的表示是小程序
 *
 */
@Slf4j
@Service

public class MiniAppServiceImpl extends SuperServiceImpl<ConfigMapper, Config> implements MiniAppService {


    @Autowired
    FileUploadApi fileUploadApi;

    protected static String thirdpartyCode2Session = "https://api.weixin.qq.com/sns/component/jscode2session" +
            "?component_access_token=COMPONENT_ACCESS_TOKEN" +
            "&appid=APPID" +
            "&grant_type=authorization_code" +
            "&component_appid=COMPONENT_APPID" +
            "&js_code=JS_CODE";

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    AppComponentTokenService appComponentTokenService;

    @Override
    public List<TemplateInfo> getTemplate(String appId) {
        WxMaService maService = MiniAppServiceHolder.getWxMaService(appId);
        WxMaSubscribeService subscribeService = maService.getSubscribeService();
        try {
            List<TemplateInfo> templateList = subscribeService.getTemplateList();
            return templateList;
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String generatescheme(String appId) {

        WxMaService maService = MiniAppServiceHolder.getWxMaService(appId);
        WxMaLinkService linkService = maService.getLinkService();
        GenerateUrlLinkRequest generateUrlLinkRequest = new GenerateUrlLinkRequest();
        generateUrlLinkRequest.setExpireType(1);
        generateUrlLinkRequest.setExpireInterval(30);
        try {
            String urlLink = linkService.generateUrlLink(generateUrlLinkRequest);
            return urlLink;
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public List<PubTemplateKeyword> getTemplate(String appId, String templateId) {
        WxMaService maService = MiniAppServiceHolder.getWxMaService(appId);
        WxMaSubscribeService subscribeService = maService.getSubscribeService();
        try {
            List<PubTemplateKeyword> keyWordsById = subscribeService.getPubTemplateKeyWordsById(templateId);
            return keyWordsById;
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 发送订阅的模版消息
     * @param appId
     * @param wxMaKfMessageDto
     */
    @Override
    public void sendMessage(String appId, WxMaSubscribeMessage wxMaKfMessageDto) {
        WxMaService maService = MiniAppServiceHolder.getWxMaService(appId);
        WxMaMsgService msgService = maService.getMsgService();

        try {
            msgService.sendSubscribeMsg(wxMaKfMessageDto);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendCustomMessage(WxMaKfMessageDto wxMaKfMessageDto) {
        String appId = wxMaKfMessageDto.getAppId();
        String openId = wxMaKfMessageDto.getOpenId();
        String pagePath = wxMaKfMessageDto.getPagePath();
        String title = wxMaKfMessageDto.getTitle();
        WxMaService maService = MiniAppServiceHolder.getWxMaService(appId);
        WxMaMsgService msgService = maService.getMsgService();
        WxMaKefuMessage message = new WxMaKefuMessage();
        message.setToUser(openId);
        message.setMsgType(WxMaConstants.KefuMsgType.MA_PAGE);
        WxMaKefuMessage.KfMaPage.KfMaPageBuilder builder = WxMaKefuMessage.KfMaPage.builder();
        builder.pagePath(pagePath);
        builder.title(title);
        WxMaKefuMessage.KfMaPage maPage = builder.build();
        message.setMaPage(maPage);
        try {
            msgService.sendKefuMsg(message);
        } catch (WxErrorException e) {
            log.error(" sendCustomMessage appId: {}, openId: {}, title: {} ", appId, openId, title);
            e.printStackTrace();
        }
    }

    /**
     * 查询所有的小程序
     * @return
     */
    @Override
    public List<Config> listAllMiniAppWithoutTenant() {

        return baseMapper.listAllMiniAppWithoutTenant();
    }

    /**
     * 根据小程序ID查询小程序
     * @param appId
     * @return
     */
    @Override
    public Config selectByAppIdWithoutTenant(String appId) {
        List<Config> configList = baseMapper.selectByAppIdWithoutTenant(appId);
        if (CollUtil.isNotEmpty(configList)) {
            return configList.get(0);
        }
        return null;
    }


    public static File catImage(File file) {
        // 图片路径
        String dir = System.getProperty("java.io.tmpdir");
        String filePath = "/saas/qrcodeTemp/";
        File folder = new File(dir + filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String outputImagePath = dir + filePath + UUID.randomUUID().toString() + ".png";

        // 裁剪区域的坐标和尺寸
        int x = 15;  // 起始 x 坐标
        int y = 15;  // 起始 y 坐标
        int width = 252;  // 裁剪宽度
        int height = 252; // 裁剪高度

        try {
            // 读取原始图片
            BufferedImage originalImage = null;
            try {
                originalImage = ImageIO.read(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 检查裁剪区域是否超出图片边界
            if (x + width > originalImage.getWidth() || y + height > originalImage.getHeight()) {
                System.out.println("裁剪区域超出图片边界");
                return file;
            }

            // 裁剪图片
            BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);

            // 保存裁剪后的图片
            File outputFile = new File(outputImagePath);
            ImageIO.write(croppedImage, "png", outputFile);
            System.out.println(outputImagePath);
            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 创建小程序二维码
     * @param appId 小程序的appId
     * @param path 路径
     * @param width 宽度
     */
    @Override
    public String createQRCode(String appId, String path, Integer width) {
        List<Config> configs = baseMapper.selectByAppIdWithoutTenant(appId);

        if (CollUtil.isNotEmpty(configs)) {
            Config config = configs.get(0);
            if (config.getThirdAuthorization()) {
                // 暂不实现
            } else {
                WxMaService maService = MiniAppServiceHolder.getWxMaService(appId);
                WxMaQrcodeService qrcodeService = maService.getQrcodeService();
                try {
                    File qrcode = qrcodeService.createQrcode(path, width);
                    File cattedImage = catImage(qrcode);
                    MockMultipartFile multipartFile = FileUtils.fileToFileItem(cattedImage);
                    R<com.caring.sass.file.entity.File> uploaded = fileUploadApi.upload(1L, multipartFile);
                    if (uploaded.getIsSuccess()) {
                        return uploaded.getData().getUrl();
                    }
                } catch (WxErrorException e) {
                    log.info("createQRCode error {}", e.getError());
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    /**
     * 第三方平台代商家 小程序登录
     * 小程序登录
     * @param js_code wx.login 获取的 code
     * @param appId 小程序appId
     * 返回参数
     */
    @Override
    public WxMaJscode2SessionResult miniAppThirdPartyCode2Session(String js_code, String appId) {

        List<Config> configs = baseMapper.selectByAppIdWithoutTenant(appId);
        if (CollUtil.isNotEmpty(configs)) {
            Config config = configs.get(0);
            // 如果是第三方平台的小程序
            if (config.getThirdAuthorization()) {

                String componentAppId = ApplicationProperties.getThirdPlatformComponentAppId();
                String componentAccessToken = appComponentTokenService.getComponentAccessToken(componentAppId);

                String getAccessTokenByCode = thirdpartyCode2Session
                        .replace("COMPONENT_APPID", componentAppId)
                        .replace("APPID",  appId)
                        .replace("JS_CODE", js_code)
                        .replace("COMPONENT_ACCESS_TOKEN", componentAccessToken);
                log.error("getAccess_tokenByCode: {}", getAccessTokenByCode);

                String s = HttpUtils.get(getAccessTokenByCode);
                if (s != null && s.contains("errcode")) {
                    log.error("获取用户的accessToken失败, 返回异常信息{}" , s);
                } else {
                    WxMaJscode2SessionResult sessionInfo = WxMaJscode2SessionResult.fromJson(s);
                    if (Objects.nonNull(sessionInfo) && StrUtil.isNotEmpty(sessionInfo.getOpenid())) {
                        redisTemplate.boundHashOps(SaasRedisBusinessKey.MINI_APP_USER_SESSION_KEY).put(sessionInfo.getOpenid(), sessionInfo.getSessionKey());
                        sessionInfo.setSessionKey(null);
                    }
                    return sessionInfo;
                }
                return null;
            } else {
                WxMaService maService = MiniAppServiceHolder.getWxMaService(appId);
                WxMaUserService userService = maService.getUserService();
                WxMaJscode2SessionResult sessionInfo = null;
                try {
                    sessionInfo = userService.getSessionInfo(js_code);
                } catch (WxErrorException e) {
                    log.info("miniAppThirdPartyCode2Session error {}", e.getError());
                    e.printStackTrace();
                }
                if (Objects.nonNull(sessionInfo) && StrUtil.isNotEmpty(sessionInfo.getOpenid())) {
                    redisTemplate.boundHashOps(SaasRedisBusinessKey.MINI_APP_USER_SESSION_KEY).put(sessionInfo.getOpenid(), sessionInfo.getSessionKey());
                    sessionInfo.setSessionKey(null);
                }
                return sessionInfo;
            }

        }
        return null;

    }

    @Override
    public WxMaPhoneNumberInfo getPhoneNumber(String code, String appId) {
        WxMaService maService = MiniAppServiceHolder.getWxMaService(appId);
        WxMaUserService userService = maService.getUserService();
        try {
            return userService.getPhoneNoInfo(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Config getByAppIdNoTenantCode(String appId) {
        List<Config> configs = baseMapper.selectByAppIdWithoutTenant(appId);
        if (CollUtil.isNotEmpty(configs)) {
            return configs.get(0);
        }
        return null;
    }

    /**
     * 分页查询小程序
     * @param buildPage
     * @param wrapper
     * @return
     */
    @Override
    public IPage<Config> pageNoTenantCode(IPage buildPage, LambdaQueryWrapper<Config> wrapper) {
        IPage<Config> selectPage = baseMapper.selectPage(buildPage, wrapper);
        return selectPage;
    }
}

