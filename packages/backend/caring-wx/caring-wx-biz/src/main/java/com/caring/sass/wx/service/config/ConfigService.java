package com.caring.sass.wx.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.wx.dto.config.*;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.entity.config.Config;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 微信配置信息
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface ConfigService extends SuperService<Config> {

    /**
     * @return com.caring.sass.wx.entity.config.Config
     * @Author yangShuai
     * @Description 通过 原始Id 获取到微信公众号信息
     * @Date 2020/9/15 17:14
     */
    Config getConfigBySourceId(String sourceId);


    /**
     * @return com.caring.sass.wx.entity.config.Config
     * @Author yangShuai
     * @Description 获取微信公众号
     * @Date 2020/9/15 17:19
     */
    Config getConfigByAppId(String appId);


    /**
     * @return java.util.List<me.chanjar.weixin.mp.bean.tag.WxUserTag>
     * @Author yangShuai
     * @Description 获取微信公众号标签列表
     * @Date 2020/9/16 9:34
     */
    List<WxUserTag> loadTagList(String appId);


    /**
     * @return void
     * @Author yangShuai
     * @Description 获取微信公众号标签列表
     * @Date 2020/9/16 15:38
     */
    List<WxUserTag> loadTagList(GeneralForm generalForm);

    /**
     * @return me.chanjar.weixin.mp.bean.tag.WxUserTag
     * @Author yangShuai
     * @Description 创建微信标签
     * @Date 2020/9/16 9:35
     */
    WxUserTag createTagIfNotExist(String appId, String tagName);

    /**
     * @param pubnoAppId
     * @param ticket     生成二维码所需要的ticket
     * @param expire
     * @return java.lang.String
     * @Author yangShuai
     * @Description
     * @Date 2020/9/15 17:22
     */
    String generateConfigQrCode(String pubnoAppId, String ticket, Integer expire);


    /**
     * @return boolean
     * @Author yangShuai
     * @Description 保存微信公众号信息
     * @Date 2020/9/16 14:26
     */
    boolean save(Config model);

    /**
     * 通过项目id查询微信配置信息
     */
    Config getConfigByTenant();

    /**
     * @return com.caring.sass.wx.entity.config.Config
     * @Author yangShuai
     * @Description 查询微信公众号信息
     * @Date 2020/9/16 14:26
     */
    Config getConfig(Config config);

    /**
     * @return java.lang.String
     * @Author yangShuai
     * @Description 查询授权文件地址
     * @Date 2020/9/16 14:26
     */
    String findTxtValueByTxtName();


    /**
     * @return void
     * @Author yangShuai
     * @Description 发布菜单到 微信公众号
     * @Date 2020/9/15 17:47
     */
    @Deprecated
    void publishMenu(Config config, JSONArray menu) throws WxErrorException;


    boolean publishMenu(Config config, WxMpMenu menu) throws WxErrorException;

    /**
     * @return void
     * @Author yangShuai
     * @Description 删除菜单
     * @Date 2020/9/16 15:07
     */
    void deleteMenu(String appId, String menuId);

    /**
     * @return com.alibaba.fastjson.JSONObject
     * @Author yangShuai
     * @Description 获取 微信公众号菜单
     * @Date 2020/9/16 15:07
     */
    JSONObject getMenu(String appId);


    WxMpMenu getMenu();

    /**
     * @return com.caring.sass.base.R
     * @Author yangShuai
     * @Description 发送微信公众号模板消息
     * @Date 2020/9/16 15:08
     */
    R sendTemplateMessage(SendTemplateMessageForm form);

    /**
     * @return java.util.List<me.chanjar.weixin.mp.bean.template.WxMpTemplate>
     * @Author yangShuai
     * @Description 获取微信公众号的模板列表
     * @Date 2020/9/16 15:08
     */
    List<WxMpTemplate> loadTemplateMessage(GeneralForm generalForm);

    /**
     * @return com.caring.sass.base.R
     * @Author yangShuai
     * @Description 发送微信公众号客服消息
     * @Date 2020/9/16 15:09
     */
    R sendKefuMsg(SendKefuMsgForm paramSendKefuMsgForm);

    /**
     * 上传图文消息内的图片获取URL
     * 使用 url 下载图片 然后上传到 微信的素材库去
     * 用于解析cms内容中出现第三方图片路径时的解决办法
     * url: https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN
     * @param appId
     * @param fileName
     * @param imgUrl
     */
    String mediaImgUpload(String appId, String fileName, String imgUrl);

    /**
     * 新增非图文永久素材
     * @param appId
     * @param material
     * @return
     */
    WxMpMaterialUploadResult materialFileUpload(String appId,  UploadTemporaryMatrialForm material);

    /**
     * 新增永久图文素材
     * url：https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=ACCESS_TOKEN
     * @param appId
     * @param news
     * @return
     */
    WxMpMaterialUploadResult mediaAddNews(String appId, WxMpMaterialNews news);

    /**
     * 根据 openId 群发图文消息
     * @param news
     */
    R<WxMpMassSendResult> massOpenIdsMessageSend(WxMpMassOpenIdsMessage message, WxMpMassTagMessage tagMessage, WxMpMassNews news);

    /**
     * 根据openId 群发 文本、语音、图片
     * @param message
     * @return
     */
    R<WxMpMassSendResult> massOpenIdsMessageSend(WxMpMassOpenIdsMessage message);


    /**
     * 预览 图文， 视频 的群发消息
     * @param message
     * @param news
     * @param video
     * @return
     */
    R<WxMpMassSendResult> massMessagePreview(WxMpMassPreviewMessage message,
                                          WxMpMassNews news,
                                          WxMpMassVideo video);

    /**
     * 根据openId 群发视频
     * @param message
     * @return
     */
    R<WxMpMassSendResult> massOpenIdsMessageSend(WxMpMassOpenIdsMessage message, WxMpMassTagMessage tagMessage , WxMpMassVideo video);

    /**
     * 上传图文消息内的图片获取URL
     * 直接上传 图片文件 到微信 图文素材
     * url: https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN
     * @param appId
     * @param simpleFile
     */
    String mediaImgUpload(String appId, MultipartFile simpleFile);

    /**
     * @return me.chanjar.weixin.common.bean.result.WxMediaUploadResult
     * @Author yangShuai
     * @Description 上传临时素材到微信公众号
     * @Date 2020/9/16 15:04
     */
    WxMediaUploadResult uploadTemporaryMatrial(String appId, UploadTemporaryMatrialForm mediaDto);

    /**
     * @return me.chanjar.weixin.common.bean.WxJsapiSignature
     * @Author yangShuai
     * @Description
     * @Date 2020/9/16 15:09
     */
    WxJsapiSignature signature(SignatureForm paramSignatureForm);

    /**
     *
     * @Author yangShuai
     * @Description 获取微信信息
     * @Date 2020/9/16 15:26
     */
    WxOAuth2UserInfo oauth2getUserInfo(Oauth2getUserInfoForm oauth2getUserInfoForm);

    /**
     * @return void
     * @Author yangShuai
     * @Description 获取accessToken
     * @Date 2020/9/16 15:26
     */
    WxOAuth2UserInfo oauth2RefreshAccessToken(Oauth2getUserInfoForm oauth2getUserInfoForm);

    /**
     * @return me.chanjar.weixin.common.bean.result.WxMediaUploadResult
     * @Author yangShuai
     * @Description 上传临时素材到微信公众号
     * @Date 2020/9/16 15:30
     */
    WxMediaUploadResult uploadTemporaryMatrial(UploadTemporaryMatrialForm paramUploadTemporaryMatrialForm);


    /**
     * @return me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult
     * @Author yangShuai
     * @Description 上传永久素材到 微信公众号
     * @Date 2020/9/17 14:58
     */
    WxMpMaterialUploadResult uploadPermanentMatrial(UploadTemporaryMatrialForm paramUploadTemporaryMatrialForm);

    /**
     * @return java.lang.Boolean
     * @Author yangShuai
     * @Description 删除 微信永久素材
     * @Date 2020/9/17 15:27
     */
    Boolean deleteMaterial(String materialId);

    /**
     * @return java.lang.String
     * @Author yangShuai
     * @Description 下载微信素材
     * @Date 2020/9/16 15:53
     */
    String downloadMatrial(DownloadMatrialForm downloadMatrialForm);

    String batchGet(int offset, int count);

    /**
     * @return void
     * @Author yangShuai
     * @Description 给用户绑定微信标签
     * @Date 2020/9/16 15:52
     */
    void bindUserTags(BindUserTagsForm bindUserTagsForm);

    /**
     *
     * @Author yangShuai
     * @Description 获取微信用户信息
     * @Date 2020/9/16 15:52
     */
    WxMpUser getFollowerInfo(GetFollowerInfoForm getFollowerInfoForm);

    /**
     * @return com.caring.sass.wx.dto.config.QrCodeDto
     * @Author yangShuai
     * @Description 一个公众号的关注者创建永久二维码
     * @Date 2020/9/16 15:53
     */
    QrCodeDto createFollowerPermanentQrCode(CreateFollowerPermanentQrCode createFollowerPermanentQrCode);

    /**
     * @return com.caring.sass.base.R
     * @Author yangShuai
     * @Description 创建授权url
     * @Date 2020/9/16 16:25
     */
    R createSilentAuthUrl(CreateSilentAuthUrlForm createSilentAuthUrlForm);

    /**
     * @return com.caring.sass.base.R<java.lang.Object>
     * @Author yangShuai
     * @Description 微信会员授权登录
     * @Date 2020/9/16 16:25
     */
    R<Object> slientAuthLoginCheck(SlientAuthLoginCheckForm slientAuthLoginCheckForm);

    /**
     * @return void
     * @Author yangShuai
     * @Description 多条件删除 公众号信息
     * @Date 2020/9/16 16:32
     */
    void deleteConfig(Config config);

    /**
     * @return void
     * @Author yangShuai
     * @Description 复制项目时 保存微信公众号信息
     * @Date 2020/9/16 16:36
     */
    void saveNotCheck(Config config);

    R varifyTokenForVerifyDomainName(VarifyTokenForVerifyDomainNameForm varifyTokenForVerifyDomainNameForm);

    /**
     * 查询微信配置的菜单
     *
     * @param appId 项目appdId
     */
    WxMpMenu queryWxMenu(String appId);

    /**
     * 发布菜单到微信
     *
     * @param appId      微信appId
     * @param wxMenuJson 微信菜单
     */
    void publishMenu2Wx(String appId, String wxMenuJson);

    /**
     * 查询系统所有微信配置信息
     */
    List<Config> listAllConfigWithoutTenant();

    /**
     * 通过appId查询
     */
    List<Config> queryByAppIdWithoutTenant(String appId);

    /**
     * 获取用户openId
     *
     * @param appId 微信appId
     * @param code  code
     * @return openId
     */
    String getWxUserOpenId(String appId, String code);

    WxOAuth2UserInfo getWxUser(String code);

    /**
     * 获取微信用户信息，当前面引导授权时的scope是snsapi_userinfo的时候才可以.
     *
     * @param appId 微信appId
     * @param code  code
     * @return 微信用户信息
     */
    WxOAuth2UserInfo getWxUser(String appId, String code);

    /**
     * 创建调用jsapi时所需要的签名
     *
     * @param appId 微信appId
     * @param url   当前网页的URL，不包含#及其后面部分
     * @return 调用jsapi时所需要的签名
     */
    WxJsapiSignature createJsapiSignature(String appId, String url);

    /**
     * 翻译华为云音频文件
     * @param voiceUrl
     * @param lang
     * @return
     */
    String addvoicetorecofortext(String voiceUrl, AiLangType lang);

    WxMpMaterialNewsBatchGetResult batchgetMmaterial(int offset, int count);


    String get_material(DownloadMatrialForm downloadMatrialForm);


    R<WxMpMassSendResult> massTagMessageSend(WxMpMassTagMessage message);

    /**
     * 统计微信标签下有多少人员
     * @param notificationTarget
     * @return
     */
    Map<NotificationTarget, Integer> countWxTagUser(NotificationTarget notificationTarget) throws WxErrorException;


    String queryWxTagUser(TagsEnum tagsEnum) throws WxErrorException;

    /**
     * 从行业模板库选择模板到帐号后台，获得模板ID的过程可在微信公众平台后台完成。为方便第三方开发者，提供通过接口调用的方式来获取模板ID，具体如下
     * @param templateId
     */
    String apiAddTemplate(String templateId, List<String> keywordNameList);


    List<String> checkFolderShareUrlExist(String url);

    /**
     * 公众号迁移查询openId在新公众号的openId
     * @param officialAccountMigrationDTO
     * @return
     */
    Map<String, String> officialAccountMigration(OfficialAccountMigrationDTO officialAccountMigrationDTO);


    /**
     * 初始化医助的微信菜单
     */
    void initAssistantMenu();


    /**
     * 发送游客注册的客服消息
     * @param subscribeDto
     */
    void sendTouristRegister(WxSubscribeDto subscribeDto, String tenantCode);

    /**
     * 个人服务号发布菜单
     * @param config
     * @param menuJson
     */
    void personalServiceNumberMenu(Config config, WxMpMenu menuJson) throws WxErrorException;
}
