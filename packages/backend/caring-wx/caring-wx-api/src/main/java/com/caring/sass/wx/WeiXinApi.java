package com.caring.sass.wx;

import com.caring.sass.base.R;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.wx.dto.config.*;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import com.caring.sass.wx.hystrix.WeiXinApiFallback;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * @ClassName com.caring.sass.wx.IWeiXinService
 * @Description 对外服务接口
 * @Author yangShuai
 * @Date 2020/9/16 9:49
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.oauth-server:caring-wx-server}", path = "/config",
        qualifier = "WeiXinApi", fallback = WeiXinApiFallback.class)
public interface WeiXinApi {

    @ApiOperation("【微信公众号】 - 返回一个公众号的详细信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "pubnoDto", value = "查询参数，支持通过ID获取、WxAppId获取、原始ID获取，此参数可以为空", required = false, dataTypeClass = Config.class, dataType = "Config")})
    @PostMapping({"/info"})
    R<Config> getConfig(@RequestBody Config config);


    @ApiOperation("【微信公众号】 - 返回一个公众号的授权文件")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "pubnoDto", value = "查询参数，支持通过ID获取、WxAppId获取、原始ID获取，此参数可以为空", required = false, dataTypeClass = Config.class, dataType = "Config")})
    @GetMapping({"/findTxtValueByTxtName"})
    R<String> findTxtValueByTxtName();


    @ApiOperation("【微信公众号】 - 返回一个公众号的二维码信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "参数，需要传递pubnoAppId", required = true)})
    @PostMapping({"/qrcode"})
    R<String> generateConfigQrCode(@RequestBody GenerateConfigQrCodeForm generateConfigQrCodeForm);


    @ApiOperation("【模板消息】 - 发送模板消息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "要推送的模板消息", required = true)})
    @PostMapping({"/template_message/send"})
    R<String> sendTemplateMessage(@RequestBody SendTemplateMessageForm sendTemplateMessageForm);


    @ApiOperation("【客服消息】 - 发送客服消息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "需要发送的客服消息", required = true, dataTypeClass = SendKefuMsgForm.class, dataType = "SendKefuMsgForm")})
    @PostMapping({"/kefu_message/send"})
    R<Boolean> sendKefuMsg(@RequestBody SendKefuMsgForm paramSendKefuMsgForm);


    @ApiOperation("【JSSDK】 - 生成签名授权的url，用于静默授权登录")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "需要签名的参数", required = true, dataTypeClass = SignatureForm.class, dataType = "SignatureForm")})
    @PostMapping({"/jssdk/signature"})
    R<WxJsapiSignature> signature(@RequestBody SignatureForm paramSignatureForm);

    @ApiOperation(value = "【OAuth2接口】 - 微信回调时会返回code给应用，应用通过code调用微信的oauth2getUserInfo接口来从微信公众平台获取用户信息", notes = "本方法是通过oauth2获取用户信息，需要购买微信高级接口使用权限，消费300元")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "参数", required = true, dataTypeClass = Oauth2getUserInfoForm.class, dataType = "Oauth2getUserInfoForm")})
    @PostMapping({"/oauth2/userinfo"})
    R<WxOAuth2UserInfo> oauth2getUserInfo(@RequestBody Oauth2getUserInfoForm paramOauth2getUserInfoForm);

    @ApiOperation(value = "【OAuth2接口】 - 刷新AccessToken", notes = "本方法是通过oauth2获取用户信息，需要购买微信高级接口使用权限，消费300元")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "参数", required = true, dataTypeClass = Oauth2getUserInfoForm.class, dataType = "Oauth2getUserInfoForm")})
    @PostMapping({"/oauth2/refresh_access_token"})
    R<WxOAuth2UserInfo> oauth2RefreshAccessToken(@RequestBody Oauth2getUserInfoForm paramOauth2getUserInfoForm);


    @ApiOperation("【上传下载】 - 【临时素材】 上传素材到公众号")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "要上传的素材信息form", required = true, dataTypeClass = UploadTemporaryMatrialForm.class, dataType = "UploadTemporaryMatrialForm")})
    @PostMapping({"/material/upload"})
    R<WxMediaUploadResult> uploadTemporaryMatrial(@RequestBody UploadTemporaryMatrialForm paramUploadTemporaryMatrialForm);


    @ApiOperation("【上传下载】 - 下载素材")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "参数", required = true, dataTypeClass = DownloadMatrialForm.class, dataType = "DownloadMatrialForm")})
    @PostMapping({"/material/download"})
    R<String> downloadMatrial(@RequestBody DownloadMatrialForm downloadMatrialForm);

    @ApiOperation("【标签】 - 返回标签列表")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "参数，可为空", required = false, dataTypeClass = GeneralForm.class, dataType = "GeneralForm")})
    @PostMapping({"/tag/list"})
    R<List<WxUserTag>> loadTagList(@RequestBody GeneralForm generalForm);


    @ApiOperation("【标签】 - 为用户绑定标签")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "绑定用户标签的参数", required = true, dataTypeClass = BindUserTagsForm.class, dataType = "BindUserTagsForm")})
    @PostMapping({"/tag/bind"})
    R<Void> bindUserTags(@RequestBody BindUserTagsForm bindUserTagsForm);

    @ApiOperation("【关注者】 - 通过微信平台拉取一个关注者的信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "用户的ID", required = true, dataTypeClass = GetFollowerInfoForm.class, dataType = "GetFollowerInfoForm")})
    @PostMapping({"/followers/info"})
    R<WxMpUser> getFollowerInfo(@RequestBody GetFollowerInfoForm getFollowerInfoForm);

    @ApiOperation("【关注者】 - 为 一个公众号的关注者创建永久二维码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "参数，用于生成二维码中链接时携带的参数", required = true, dataTypeClass = CreateFollowerPermanentQrCode.class, dataType = "CreateFollowerPermanentQrCode")})
    @PostMapping({"/followers/qrcode/create"})
    R<QrCodeDto> createFollowerPermanentQrCode(@RequestBody CreateFollowerPermanentQrCode paramCreateFollowerPermanentQrCode);

    @ApiOperation("【静默授权】 - 创建一个静默授权的URL")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "参数，需要传递重定向url", required = true, dataTypeClass = CreateSilentAuthUrlForm.class, dataType = "CreateSilentAuthUrlForm")})
    @PostMapping({"/create_silent_auth_url"})
    R<String> createSilentAuthUrl(@RequestBody CreateSilentAuthUrlForm createSilentAuthUrlForm);


    @ApiOperation("【静默授权】 - 登录")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "参数，必须传递code进来", required = true, dataTypeClass = SlientAuthLoginCheckForm.class, dataType = "SlientAuthLoginCheckForm")})
    @PostMapping({"/silent_auth/login"})
    R<Object> slientAuthLoginCheck(@RequestBody SlientAuthLoginCheckForm slientAuthLoginCheckForm);


    @ApiOperation("【静默授权】 - 创建一个静默授权的URL")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "form", value = "参数，微信回调时传递的各种参数", required = true, dataTypeClass = VarifyTokenForVerifyDomainNameForm.class, dataType = "varifyTokenForVerifyDomainNameForm")})
    @PostMapping({"/varify_token"})
    R<String> varifyTokenForVerifyDomainName(@RequestBody VarifyTokenForVerifyDomainNameForm paramVarifyTokenForVerifyDomainNameForm);


    @ApiOperation("删除一个公众号")
    @PostMapping({"/deleteConfig"})
    R<Void> deletePubno(@RequestBody Config config);


    @ApiOperation("保存一个没有校验的微信公众号信息")
    @PostMapping({"/saveNotCheckConfig"})
    R<Void> saveNotCheck(@RequestBody Config config);

    /**
     * 批量查询
     *
     * @param data 批量查询
     * @return 查询结果
     */
    @PostMapping("/query")
    R<List<Config>> query(@RequestBody Config data);


    /**
     * 存在更新记录，否插入一条记录
     * model 保存实体
     *
     * @return 实体
     */
    @PostMapping(value = "saveOrUpdate")
    R<Config> saveOrUpdate(@RequestBody ConfigSaveDTO model);

    /**
     * 查询微信配置的菜单
     *
     * @param appId 项目appdId
     */
    @GetMapping("queryWxMenu")
    R<String> queryWxMenu(@RequestParam(value = "appId") String appId);

    /**
     * 发布菜单到微信
     */
    @PostMapping("publishMenu2Wx")
    R<Boolean> publishMenu2Wx(@RequestBody PublishMenuDTO publishMenuDTO);

    /**
     * 获取用户openId
     *
     * @param appId 微信appId
     * @param code  code
     * @return openId
     */
    @GetMapping("getWxUserOpenId")
    R<String> getWxUserOpenId(@RequestParam(value = "appId") String appId,
                              @RequestParam(value = "code") String code);

    /**
     * 获取微信用户信息
     *
     * @param appId 微信appId
     * @param code  code
     * @return openId
     */
    @ApiOperation(value = "获取微信用户信息")
    @GetMapping("getWxUser")
    R<WxOAuth2UserInfo> getWxUser(@RequestParam(value = "appId") String appId,
                                  @RequestParam(value = "code") String code);

    @ApiOperation(value = "上传图文消息内的图片获取URL")
    @PostMapping("mediaImgUpload/simpleFile")
    R<String> mediaImgUpload(@RequestParam(value = "file") MultipartFile simpleFile);


    @ApiOperation(value = "通过url将上传图文消息内的图片获取微信得URL")
    @PostMapping("mediaImgUpload/fileUrl")
    R<String> mediaImgUpload(@RequestParam("fileName") String fileName, @RequestParam("fileUrl") String fileUrl);

    @ApiOperation(value = "新增非图文永久素材")
    @PostMapping("materialFileUpload")
    R<WxMpMaterialUploadResult> materialFileUpload(@RequestBody UploadTemporaryMatrialForm material);

    @ApiOperation(value = "删除永久素材")
    @PostMapping("deleteMedia")
    R<Boolean> deleteMedia(@RequestParam("mediaId") String mediaId);

    @ApiOperation(value = "标签、all、openids群发图文消息")
    @PostMapping("massMessageSendMpNews")
    R<WxMpMassSendResult> massMessageSend(@RequestBody MassMpNewsMessage massMpNewsMessage);


    @ApiOperation(value = "标签、all、openids群发视频消息")
    @PostMapping("massMessageSendVideo")
    R<WxMpMassSendResult> massMessageSend(@RequestBody MassVideoMessage massVideoMessage);

    @ApiOperation(value = "openids群发其他类型消息")
    @PostMapping("massOpenIdsMessageSendOther")
    R<WxMpMassSendResult> massOpenIdsMessageSend(@RequestBody WxMpMassOpenIdsMessage message);

    @ApiOperation(value = "标签、all群发其他类型消息")
    @PostMapping("massTagMessageSend")
    R<WxMpMassSendResult> massTagMessageSend(@RequestBody WxMpMassTagMessage message);

    @ApiOperation(value = "群发预览")
    @PostMapping("massMessagePreview")
    R<WxMpMassSendResult> massMessagePreview(@RequestBody MassMessagePreview preview);


    @ApiOperation(value = "获取用户的微信信息")
    @GetMapping("getWeiXinUserInfo")
    R<WeiXinUserInfo> getWeiXinUserInfo(@RequestParam("openId") String openId);

    @ApiOperation(value = "统计微信标签下各有多少人员")
    @GetMapping("countWxTagUser")
    R<Map<NotificationTarget, Integer>> countWxTagUser(@RequestParam("notificationTarget") NotificationTarget notificationTarget);

    @ApiOperation(value = "查询微信标签下的人员并暂时存入redis")
    @GetMapping("queryWxTagUser")
    R<String> queryWxTagUser(@RequestParam("tagsEnum") TagsEnum tagsEnum);


    /**
     * 批量获取微信用户信息
     */
    @PostMapping("/batchGetUserInfo")
    R<List<WxMpUser>> batchGetUserInfo(@RequestBody SyncUserUnionIdDTO userUnionIdDTO);


    @ApiOperation("【微信菜单】 - 检查文件夹链接是否存在于微信菜单")
    @GetMapping({"/checkFolderShareUrlExist"})
    R<List<String>> checkFolderShareUrlExist(@RequestParam("url") String url);

    @ApiOperation(value = "公众号迁移查询openId在新公众号的openId")
    @PostMapping("officialAccountMigration")
    R<Map<String, String>> officialAccountMigration(@RequestBody OfficialAccountMigrationDTO officialAccountMigrationDTO);


    @ApiOperation(value = "患者删除或医生退出登录清除微信信息")
    @GetMapping("/deleteWeiXinUserInfo")
    R<Boolean> deleteWeiXinUserInfo(@RequestParam(name = "openId") String openId);

}
