package com.caring.sass.wx.controller.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.oauth.api.WXCallBackApi;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.wx.config.WxMpServiceHolder;
import com.caring.sass.wx.dto.config.*;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import com.caring.sass.wx.service.config.ConfigService;
import com.caring.sass.wx.service.config.WeiXinUserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpUserService;
import me.chanjar.weixin.mp.bean.*;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNewsBatchGetResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import me.chanjar.weixin.mp.enums.AiLangType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 微信配置信息
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/config")
@Api(value = "Config", tags = "微信配置信息")
//@PreAuth(replace = "config:")
public class ConfigController extends SuperController<ConfigService, Long, Config, ConfigPageDTO, ConfigSaveDTO, ConfigUpdateDTO> {

    @Autowired
    WeiXinUserInfoService weiXinUserInfoService;
    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<Config> configList = list.stream().map((map) -> {
            Config config = Config.builder().build();
            //TODO 请在这里完成转换
            return config;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(configList));
    }

    @ApiOperation("【微信公众号】 - 返回一个公众号的详细信息")
    @PostMapping({"/info"})
    public R<Config> getConfig(@RequestBody Config config) {
        Config serviceConfig = baseService.getConfig(config);
        return R.success(serviceConfig);
    }


    @Deprecated
    @ApiOperation("【微信公众号】 - 查找公众号文件内容")
    @GetMapping({"/findWxAuthorizationContent"})
    public R<String> findWxAuthorizationContent() {
        String valueByTxtName = baseService.findTxtValueByTxtName();
        return R.success(valueByTxtName);
    }

    @ApiOperation("【微信公众号】 - 查询微信公众号授权信息")
    @GetMapping({"/findConfig/{tenantCode}"})
    public R<Config> findConfig(@PathVariable String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        Config configByTenant = baseService.getConfigByTenant();
        return R.success(configByTenant);
    }


    @ApiOperation("【微信公众号】 - 返回一个公众号的二维码信息")
    @PostMapping({"/qrcode"})
    public R<String> generateConfigQrCode(@RequestBody GenerateConfigQrCodeForm generateConfigQrCodeForm) {
        Config config = new Config();
        config.setAppId(generateConfigQrCodeForm.getWxAppId());
        config = baseService.getConfig(config);
        String url = baseService.generateConfigQrCode(config.getAppId(), generateConfigQrCodeForm.getTicket(), generateConfigQrCodeForm.getExpire());
        return R.success(url);
    }


    @ApiOperation("【微信菜单】 - 新版本弃用，参考")
    @PostMapping({"/menu/publish/{tenantCode}"})
    @Deprecated
    public R<Boolean> publishMenu(@PathVariable("tenantCode") String tenantCode, @RequestBody JSONArray menuJson) {
        try {
            BaseContextHandler.setTenant(tenantCode);
            Config config = baseService.getConfig(null);
            baseService.publishMenu(config, menuJson);
            return R.success();
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("【微信菜单】 - 保存菜单")
    @PostMapping({"/menu/publish_v2/{tenantCode}"})
    public R<Boolean> publishMenu_v2(@PathVariable("tenantCode") String tenantCode, @RequestBody WxMpMenu menuJson) {
        try {
            BaseContextHandler.setTenant(tenantCode);
            Config config = baseService.getConfig(null);
            baseService.publishMenu(config, menuJson);
            return R.success();
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("【个人服务号微信菜单】 - 保存菜单")
    @PostMapping({"/menu/personalServiceNumber/{tenantCode}"})
    public R<Boolean> personalServiceNumberMenu(@PathVariable("tenantCode") String tenantCode, @RequestBody WxMpMenu menuJson) {
        try {
            BaseContextHandler.setTenant(tenantCode);
            Config config = baseService.getConfig(null);
            baseService.personalServiceNumberMenu(config, menuJson);
            return R.success();
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }



    @ApiOperation("【微信菜单】 - 删除微信菜单")
    @DeleteMapping({"/menu/delete/{menuId}/{tenantCode}"})
    public R deleteMenu(@PathVariable String menuId, @PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        Config config = baseService.getConfig(null);
        this.baseService.deleteMenu(config.getAppId(), menuId);
        return R.success();
    }


    @ApiOperation("【微信菜单】 - 检查文件夹链接是否存在于微信菜单")
    @GetMapping({"/checkFolderShareUrlExist"})
    public R<List<String>> checkFolderShareUrlExist(@RequestParam String url) {

        List<String> tenantCodes = baseService.checkFolderShareUrlExist(url);
        return R.success(tenantCodes);

    }


    @ApiOperation("【微信菜单】- 新版本弃用")
    @GetMapping({"/menu/{tenantCode}"})
    @Deprecated
    public R<JSONObject> getMenu(@PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        Config config = new Config();
        Config serviceConfig = baseService.getConfig(config);
        JSONObject jsonObject = this.baseService.getMenu(serviceConfig.getAppId());
        return R.success(jsonObject);
    }

    @ApiOperation("【微信菜单】 - 新的获取菜单")
    @GetMapping({"/menu_v2/{tenantCode}"})
    public R<WxMpMenu> getMenu2(@PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        WxMpMenu wxMpMenu = this.baseService.getMenu();
        return R.success(wxMpMenu);
    }


    @ApiOperation("【模板消息】 - 发送模板消息")
    @PostMapping({"/template_message/send"})
    public R<String> sendTemplateMessage(@RequestBody SendTemplateMessageForm sendTemplateMessageForm) {
        return baseService.sendTemplateMessage(sendTemplateMessageForm);
    }





    @ApiOperation("【模板消息】 - 返回公众号的模板消息列表")
    @GetMapping({"/template_message/list/{tenantCode}"})
    public R<List<WxMpTemplate>> loadTemplateMessages(@PathVariable String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        List<WxMpTemplate> wxMpTemplates = baseService.loadTemplateMessage(new GeneralForm());
        return R.success(wxMpTemplates);
    }

    @ApiOperation("【模板消息】 - 返回公众号的模板消息)")
    @GetMapping({"/template_message"})
    public R<List<WxMpTemplate>> loadTemplateMessages() {
        List<WxMpTemplate> wxMpTemplates = baseService.loadTemplateMessage(new GeneralForm());
        return R.success(wxMpTemplates);
    }


    @ApiOperation("【客服消息】 - 发送客服消息")
    @PostMapping({"/kefu_message/send"})
    public R<Boolean> sendKefuMsg(@RequestBody SendKefuMsgForm paramSendKefuMsgForm) {

        return baseService.sendKefuMsg(paramSendKefuMsgForm);
    }

    @ApiOperation("【JSSDK】 - 无授权获取授权页ticket ")
    @PostMapping({"/jssdk/anno/signature"})
    public R<WxJsapiSignature> annoSignature(@RequestBody SignatureForm paramSignatureForm) {
        String tenantCode = paramSignatureForm.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        return this.signature(paramSignatureForm);
    }


    @ApiOperation("【JSSDK】 - 无授权获取授权页ticket使用默认appId ")
    @PostMapping({"/jssdk/anno/signature/noAppId"})
    public R<WxJsapiSignature> annoSignatureUserUserDefaultAppId(@RequestBody SignatureForm signatureForm) {
        String wxAppId = signatureForm.getWxAppId();
        Config config = null;
        if (StrUtil.isEmpty(wxAppId)) {
            config = baseService.getConfigByAppId("wx16acc384814726c2");
            if (Objects.isNull(config)) {
                config = baseService.getConfigByAppId("wx8c647412694c3120");
            }
            if (Objects.isNull(config)) {
                List<Config> configList = baseService.queryByAppIdWithoutTenant(null);
                if (CollUtil.isEmpty(configList)) {
                    throw new BizException("没有可用的公众号");
                }
                config = configList.get(0);
            }
        }
        if (Objects.isNull(config)) {
            return R.fail("没有可用的公众号");
        }
        signatureForm.setWxAppId(config.getAppId());
        return this.signature(signatureForm);
    }

    @ApiOperation("【JSSDK】 - 获取授权页ticket ")
    @PostMapping({"/jssdk/signature"})
    public R<WxJsapiSignature> signature(@RequestBody SignatureForm paramSignatureForm) {
        WxJsapiSignature signature = this.baseService.signature(paramSignatureForm);
        return R.success(signature);
    }


    @ApiOperation(value = "【OAuth2接口】 - 微信回调时会返回code给应用，应用通过code调用微信的oauth2getUserInfo接口来从微信公众平台获取用户信息")
    @PostMapping({"/oauth2/userinfo"})
    public R<WxOAuth2UserInfo> oauth2getUserInfo(@RequestBody Oauth2getUserInfoForm oauth2getUserInfoForm) {
        WxOAuth2UserInfo wxOAuth2UserInfo = baseService.oauth2getUserInfo(oauth2getUserInfoForm);
        return R.success(wxOAuth2UserInfo);
    }


    @ApiOperation(value = "【OAuth2接口】 - 刷新AccessToken", notes = "本方法是通过oauth2获取用户信息，需要购买微信高级接口使用权限，消费300元")
    @PostMapping({"/oauth2/refresh_access_token"})
    public R<WxOAuth2UserInfo> oauth2RefreshAccessToken(@RequestBody Oauth2getUserInfoForm oauth2getUserInfoForm) {
        WxOAuth2UserInfo wxOAuth2UserInfo = baseService.oauth2RefreshAccessToken(oauth2getUserInfoForm);
        return R.success(wxOAuth2UserInfo);
    }


    @ApiOperation("【上传下载】 - 【临时素材】 上传素材到公众号")
    @PostMapping({"/material/upload"})
    public R<WxMediaUploadResult> uploadTemporaryMatrial(@RequestBody UploadTemporaryMatrialForm paramUploadTemporaryMatrialForm) {
        WxMediaUploadResult wxMediaUploadResult = baseService.uploadTemporaryMatrial(paramUploadTemporaryMatrialForm);
        return R.success(wxMediaUploadResult);
    }

    @ApiOperation("【素材】 -  获取素材列表")
    @PostMapping({"/material/batchget_material"})
    public R<WxMpMaterialNewsBatchGetResult> batchgetMmaterial(@RequestParam Integer offset,
                                                    @RequestParam Integer count) {
        WxMpMaterialNewsBatchGetResult wxMediaUploadResult = baseService.batchgetMmaterial(offset, count);
        return R.success(wxMediaUploadResult);
    }


    @ApiOperation("【素材】 -  获取草稿箱列表")
    @PostMapping({"/material/draft/batchget"})
    public R<String> batchGet(@RequestParam Integer offset, @RequestParam Integer count) {
        String result = baseService.batchGet(offset, count);
        return R.success(result);
    }


    @ApiOperation("【上传下载】 - 下载素材")
    @PostMapping({"/material/download"})
    public R<String> downloadMatrial(@RequestBody DownloadMatrialForm downloadMatrialForm) {

        String matrial = baseService.downloadMatrial(downloadMatrialForm);
        return R.success(matrial);

    }

    @ApiOperation("【上传下载】 - 下载临时语音素材")
    @PostMapping({"/material/get_material"})
    public R<String> downloadMatrial1(@RequestBody DownloadMatrialForm downloadMatrialForm) {

        String matrial = baseService.get_material(downloadMatrialForm);
        return R.success(matrial);

    }


    @ApiOperation("【标签】 - 返回标签列表")
    @PostMapping({"/tag/list"})
    public R<List<WxUserTag>> loadTagList(@RequestBody GeneralForm generalForm) {

        String tenantCode = generalForm.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        List<WxUserTag> wxUserTags = baseService.loadTagList(generalForm);
        return R.success(wxUserTags);

    }


    @ApiOperation("【标签】 - 为用户绑定标签")
    @PostMapping({"/tag/bind"})
    public R bindUserTags(@RequestBody BindUserTagsForm bindUserTagsForm) {
        baseService.bindUserTags(bindUserTagsForm);
        return R.success();
    }


    @ApiOperation("【关注者】 - 通过微信平台拉取一个关注者的信息")
    @PostMapping({"/followers/info"})
    public R<WxMpUser> getFollowerInfo(@RequestBody GetFollowerInfoForm getFollowerInfoForm) {
        WxMpUser followerInfo = baseService.getFollowerInfo(getFollowerInfoForm);
        return R.success(followerInfo);
    }


    @ApiOperation("【关注者】 - 为 一个公众号的关注者创建永久二维码|临时二维码 根据是否有过期时间决定")
    @PostMapping({"/followers/qrcode/create"})
    public R<QrCodeDto> createFollowerPermanentQrCode(@RequestBody CreateFollowerPermanentQrCode createFollowerPermanentQrCode) {
        QrCodeDto qrCodeDto = baseService.createFollowerPermanentQrCode(createFollowerPermanentQrCode);
        return R.success(qrCodeDto);
    }

    @ApiOperation("【静默授权】 - 创建一个静默授权的URL")
    @PostMapping({"/create_silent_auth_url"})
    public R<String> createSilentAuthUrl(@RequestBody CreateSilentAuthUrlForm createSilentAuthUrlForm) {
        return baseService.createSilentAuthUrl(createSilentAuthUrlForm);
    }


    @ApiOperation("【静默授权】 - 登录")
    @PostMapping({"/silent_auth/login"})
    public R<Object> slientAuthLoginCheck(@RequestBody SlientAuthLoginCheckForm slientAuthLoginCheckForm) {
        return baseService.slientAuthLoginCheck(slientAuthLoginCheckForm);
    }


    @ApiOperation("【静默授权】 - 验证token是否有效")
    @PostMapping({"/varify_token"})
    public R<String> varifyTokenForVerifyDomainName(@RequestBody VarifyTokenForVerifyDomainNameForm varifyTokenForVerifyDomainNameForm) {
        return baseService.varifyTokenForVerifyDomainName(varifyTokenForVerifyDomainNameForm);
    }


    @ApiOperation("删除一个公众号")
    @PostMapping({"/deleteConfig"})
    public R deleteConfig(@RequestBody Config config) {
        baseService.deleteConfig(config);
        return R.success();
    }

    @ApiOperation("保存一个没有校验的微信公众号信息")
    @PostMapping({"/saveNotCheckConfig"})
    public R saveNotCheck(@RequestBody Config config) {
        baseService.saveNotCheck(config);
        return R.success();
    }

    /**
     * 存在更新记录，否插入一条记录
     *
     * @return 实体
     */
    @Deprecated
    @ApiOperation(value = "存在更新记录，否插入一条记录")
    @PostMapping(value = "saveOrUpdate")
    public R<Config> saveOrUpdate(@RequestBody ConfigSaveDTO model) {
        String tenant = BaseContextHandler.getTenant();
        BizAssert.notEmpty(tenant, "请输入项目编码");
        // 不能存在相同的appId
        List<Config> configs = baseService.queryByAppIdWithoutTenant(model.getAppId());
        if (CollUtil.isNotEmpty(configs)) {
            for (Config config : configs) {
                if (!Objects.equals(config.getId(), model.getId())) {
                    return R.fail("appId不能重复");
                }
            }
        }
        Config config = BeanUtil.toBean(model, Config.class);
        // 更新条件，租户存在记录则更新，否则插入
        baseService.saveOrUpdate(config);
        return R.success(config);
    }


    /**
     * 查询微信配置的菜单
     *
     * @param appId 项目appdId
     */
    @ApiOperation(value = "查询微信配置的菜单")
    @GetMapping("queryWxMenu")
    public R<String> queryWxMenu(@RequestParam(value = "appId") @NotEmpty(message = "appId不能为空") String appId) {
        WxMpMenu m = baseService.queryWxMenu(appId);
        return R.success(m.toJson());
    }

    /**
     * 发布菜单到微信
     */
    @ApiOperation(value = "发布菜单到微信")
    @PostMapping("publishMenu2Wx")
    @Deprecated
    public R<Boolean> publishMenu2Wx(@RequestBody @Validated PublishMenuDTO publishMenuDTO) {
        baseService.publishMenu2Wx(publishMenuDTO.getAppId(), publishMenuDTO.getWxMenuJson());
        return success();
    }


    /**
     * 获取用户openId
     *
     * @param appId 微信appId
     * @param code  code
     * @return openId
     */
    @ApiOperation(value = "通过code获取用户openId")
    @GetMapping("getWxUserOpenId")
    public R<String> getWxUserOpenId(@RequestParam(value = "appId") @NotEmpty(message = "appId不能为空") String appId,
                                     @RequestParam(value = "code") @NotEmpty(message = "code不能为空") String code) {
        log.error("getWxUserOpenId appId {}", appId);
        log.error("getWxUserOpenId tenantCode {}", BaseContextHandler.getTenant());
        String openId = baseService.getWxUserOpenId(appId, code);
        return R.success(openId);
    }



    @ApiOperation(value = "通过code获取用户openId")
    @GetMapping("/anno/getWxUserOpenId")
    public R<String> annoGetWxUserOpenId(@RequestParam(value = "appId") @NotEmpty(message = "appId不能为空") String appId,
                                     @RequestParam(value = "code") @NotEmpty(message = "code不能为空") String code) {
        String openId = baseService.getWxUserOpenId(appId, code);
        return R.success(openId);
    }


    /**
     * 获取微信用户信息
     *
     * @param appId 微信appId
     * @param code  code
     * @return openId
     */
    @ApiOperation(value = "通过code获取用户信息")
    @GetMapping("getWxUser")
    public R<WxOAuth2UserInfo> getWxUser(@RequestParam(value = "appId") @NotEmpty(message = "appId不能为空") String appId,
                                     @RequestParam(value = "code") @NotEmpty(message = "code不能为空") String code) {
        WxOAuth2UserInfo wxOAuth2UserInfo = baseService.getWxUser(appId, code);
        return R.success(wxOAuth2UserInfo);
    }

    /**
     * 获取微信用户信息
     *
     * @param appId 微信appId
     * @param code  code
     * @return openId
     */
    @ApiOperation(value = "无授权通过code获取用户信息")
    @GetMapping("anno/getWxUser")
    public R<WxOAuth2UserInfo> annoGetWxUser(@RequestParam(value = "appId") @NotEmpty(message = "appId不能为空") String appId,
                                 @RequestParam(value = "code") @NotEmpty(message = "code不能为空") String code) {
        WxOAuth2UserInfo wxOAuth2UserInfo = baseService.getWxUser(appId, code);
        return R.success(wxOAuth2UserInfo);
    }

    /**
     * 创建调用jsapi时所需要的签名(不校验token)
     *
     * @param appId 微信appId
     * @param url   当前网页的URL，不包含#及其后面部分
     * @return 调用jsapi时所需要的签名
     */
    @ApiOperation(value = "创建调用jsapi时所需要的签名（不校验token）")
    @GetMapping("/anno/createJsapiSignature")
    public R<WxJsapiSignature> createJsapiSignature(@RequestParam(value = "appId") @NotEmpty(message = "appId不能为空") String appId,
                                                    @RequestParam(value = "url") @NotEmpty(message = "当前网页的URL，不包含#及其后面部分不能为空") String url) {
        WxJsapiSignature wxJsapiSignature = baseService.createJsapiSignature(appId, url);
        return R.success(wxJsapiSignature);
    }


    @ApiOperation(value = "将音频文件翻译成文字")
    @GetMapping("/addvoicetorecofortext")
    public R<String> addvoicetorecofortext(@RequestParam("voiceUrl") String voiceUrl,
                                           @RequestParam(value = "lang", required = false) AiLangType lang) {

        String addvoicetorecofortext = baseService.addvoicetorecofortext(voiceUrl, lang);
        return R.success(addvoicetorecofortext);

    }

    @ApiOperation(value = "上传图文消息内的图片获取URL")
    @PostMapping("mediaImgUpload/simpleFile")
    public R<String> mediaImgUpload(@RequestParam(value = "file") MultipartFile simpleFile) {

        Config configByTenant = baseService.getConfigByTenant();
        String imgUpload = baseService.mediaImgUpload(configByTenant.getAppId(), simpleFile);
        return R.success(imgUpload);

    }

    @ApiOperation(value = "通过url将上传图文消息内的图片获取微信得URL")
    @PostMapping("mediaImgUpload/fileUrl")
    public R<String> mediaImgUpload(@RequestParam("fileName") String fileName, @RequestParam("fileUrl") String fileUrl) {

        Config configByTenant = baseService.getConfigByTenant();
        String imgUpload = baseService.mediaImgUpload(configByTenant.getAppId(), fileName, fileUrl);
        return R.success(imgUpload);

    }

    @ApiOperation(value = "新增永久图文素材")
    @PostMapping("mediaAddNews")
    public R<WxMpMaterialUploadResult> mediaAddNews(@RequestBody WxMpMaterialNews news) {

        Config configByTenant = baseService.getConfigByTenant();
        WxMpMaterialUploadResult mediaAddNews = baseService.mediaAddNews(configByTenant.getAppId(), news);
        return R.success(mediaAddNews);

    }

    @ApiOperation(value = "新增非图文永久素材")
    @PostMapping("materialFileUpload")
    public R<WxMpMaterialUploadResult> materialFileUpload(@RequestBody UploadTemporaryMatrialForm material) {
        String tenantCode = material.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        Config configByTenant = baseService.getConfigByTenant();
        WxMpMaterialUploadResult mediaAddNews = baseService.materialFileUpload(configByTenant.getAppId(), material);
        return R.success(mediaAddNews);
    }


    @ApiOperation(value = "删除永久素材")
    @PostMapping("deleteMedia")
    public R<Boolean> deleteMedia(@RequestParam("mediaId") String mediaId) {
        Boolean material = baseService.deleteMaterial(mediaId);
        return R.success(material);
    }

    @ApiOperation(value = "标签、all、openids群发图文消息")
    @PostMapping("massMessageSendMpNews")
    public R<WxMpMassSendResult> massMessageSend(@RequestBody MassMpNewsMessage massMpNewsMessage) {
        WxMpMassNews wxMpMassNews = massMpNewsMessage.getWxMpMassNews();
        WxMpMassOpenIdsMessage message = massMpNewsMessage.getMessage();
        WxMpMassTagMessage tagMessage = massMpNewsMessage.getTagMessage();
        return baseService.massOpenIdsMessageSend(message, tagMessage, wxMpMassNews);

    }

    @ApiOperation(value = "标签、all、openids群发视频消息")
    @PostMapping("massMessageSendVideo")
    public R<WxMpMassSendResult> massMessageSend(@RequestBody MassVideoMessage massVideoMessage) {
        WxMpMassVideo wxMpMassVideo = massVideoMessage.getWxMpMassVideo();
        WxMpMassOpenIdsMessage message = massVideoMessage.getMessage();
        WxMpMassTagMessage tagMessage = massVideoMessage.getTagMessage();
        return baseService.massOpenIdsMessageSend(message, tagMessage,  wxMpMassVideo);

    }

    @ApiOperation(value = "标签、all群发其他类型消息")
    @PostMapping("massTagMessageSend")
    public R<WxMpMassSendResult> massTagMessageSend(@RequestBody WxMpMassTagMessage message) {
        return baseService.massTagMessageSend(message);

    }

    @ApiOperation(value = "openids群发其他类型消息")
    @PostMapping("massOpenIdsMessageSendOther")
    public R<WxMpMassSendResult> massOpenIdsMessageSend(@RequestBody WxMpMassOpenIdsMessage message) {
        return baseService.massOpenIdsMessageSend(message);
    }

    @ApiOperation(value = "群发预览")
    @PostMapping("massMessagePreview")
    public R<WxMpMassSendResult> massMessagePreview(@RequestBody MassMessagePreview preview) {
        WxMpMassPreviewMessage previewMessage = preview.getWxMpMassPreviewMessage();
        WxMpMassNews massNews = preview.getWxMpMassNews();
        WxMpMassVideo massVideo = preview.getWxMpMassVideo();
        return baseService.massMessagePreview(previewMessage, massNews, massVideo);

    }

    @ApiOperation(value = "无授权获取用户的微信信息")
    @GetMapping("/anno/getWeiXinUserInfo")
    public R<WeiXinUserInfo> annoWeiXinUserInfo(@RequestParam("openId") String openId) {

        WeiXinUserInfo userInfo = weiXinUserInfoService.getByOpenId(openId);
        return R.success(userInfo);

    }

    @ApiOperation(value = "获取用户的微信信息")
    @GetMapping("getWeiXinUserInfo")
    public R<WeiXinUserInfo> getWeiXinUserInfo(@RequestParam("openId") String openId) {

        WeiXinUserInfo userInfo = weiXinUserInfoService.getByOpenId(openId);
        return R.success(userInfo);
    }

    @Autowired
    WXCallBackApi wxCallBackApi;


    @ApiOperation(value = "设置游客选择的身份")
    @GetMapping("/anno/setTouristUserRole")
    public R<Boolean> setTouristUserRole(@RequestParam("openId") String openId,
                                               @RequestParam(value = "unionId", required = false) String unionId,
                                               @RequestParam("userRole") String userRole) {

        String tenant = BaseContextHandler.getTenant();
        if (StrUtil.isEmpty(tenant)) {
            throw new BizException("项目信息不存在");
        }
        weiXinUserInfoService.setOpenIdUserRole(openId, userRole);
        Config configByTenant = baseService.getConfigByTenant();
        // 使用openId 调用患者入组方法。完成患者入组
        WxSubscribeDto wxSubscribeDto = new WxSubscribeDto();
        wxSubscribeDto.setOpenId(openId);
        wxSubscribeDto.setUnionId(unionId);
        wxSubscribeDto.setWxAppId(configByTenant.getAppId());
        wxSubscribeDto.setLocale(LocaleContextHolder.getLocale());
        if (UserType.UCENTER_PATIENT.equals(userRole)) {
            return wxCallBackApi.subscribe(wxSubscribeDto);
        } else if (UserType.UCENTER_DOCTOR.equals(userRole)) {
            return wxCallBackApi.doctorSubscribe(wxSubscribeDto);
        }
        return R.success(false);
    }

    @ApiOperation(value = "患者删除或医生退出登录清除微信信息")
    @GetMapping("/deleteWeiXinUserInfo")
    public R<Boolean> deleteWeiXinUserInfo(@RequestParam(name = "openId") String openId) {
        WeiXinUserInfo xinUserInfo = weiXinUserInfoService.getByOpenId(openId);
        if (Objects.isNull(xinUserInfo)) {
            return R.success(true);
        }
        xinUserInfo.setUserRole(null);
        weiXinUserInfoService.updateAllById(xinUserInfo);
        return R.success(true);
    }

    /**
     * 批量获取微信用户信息
     */
    @ApiOperation(value = "批量获取微信用户信息")
    @PostMapping("/batchGetUserInfo")
    public R<List<WxMpUser>> batchGetUserInfo(@RequestBody SyncUserUnionIdDTO userUnionIdDTO) {
        String appId = userUnionIdDTO.getAppId();
        List<String> openIdList = userUnionIdDTO.getOpenIdList();

        List<WxMpUser> r = new ArrayList<>();
        WxMpUserService userService = WxMpServiceHolder.getWxMpService(appId).getUserService();
        try {
            r = userService.userInfoList(openIdList);
        } catch (Exception e) {
            log.error("同步微信用户信息异常", e);
        }
        return R.success(r);
    }

    /**
     * 统计标签下一共多少的人员
     */
    @ApiOperation(value = "统计微信标签下各有多少人员")
    @GetMapping("countWxTagUser")
    public R<Map<NotificationTarget, Integer>> countWxTagUser(@RequestParam NotificationTarget notificationTarget) {

        Map<NotificationTarget, Integer> targetIntegerMap = null;
        try {
            targetIntegerMap = baseService.countWxTagUser(notificationTarget);
        } catch (WxErrorException e) {
            log.info("微信公众号服务异常");
        }
        return R.success(targetIntegerMap);

    }


    /**
     * 查询微信标签下的人员
     */
    @ApiOperation(value = "查询微信标签下的人员并暂时存入redis")
    @GetMapping("queryWxTagUser")
    public R<String> queryWxTagUser(@RequestParam TagsEnum tagsEnum) {
        try {
            String redisKey = baseService.queryWxTagUser(tagsEnum);
            return R.success(redisKey);
        } catch (WxErrorException e) {
            log.info("微信公众号服务异常");
        }
        return R.success(null);

    }


    @ApiOperation(value = "从模板库获取模板到本库")
    @PostMapping("apiDddTemplate")
    public R<List<WxMpTemplate>> apiDddTemplate(String templateId) {

        baseService.apiAddTemplate(templateId, null);
        GeneralForm form = new GeneralForm();
        form.setTenantCode(BaseContextHandler.getTenant());
        List<WxMpTemplate> mpTemplates = baseService.loadTemplateMessage(form);
        return R.success(mpTemplates);
    }

    @ApiOperation(value = "公众号迁移查询openId在新公众号的openId")
    @PostMapping("officialAccountMigration")
    public R<Map<String, String>> officialAccountMigration(@RequestBody OfficialAccountMigrationDTO officialAccountMigrationDTO) {

        Map<String, String> migration = baseService.officialAccountMigration(officialAccountMigrationDTO);
        return R.success(migration);
    }

}
