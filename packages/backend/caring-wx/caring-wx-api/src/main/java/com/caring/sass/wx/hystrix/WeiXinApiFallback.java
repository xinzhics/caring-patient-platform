package com.caring.sass.wx.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.*;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Component
public class WeiXinApiFallback implements WeiXinApi {
    @Override
    public R<Config> getConfig(Config config) {
        return R.timeout();
    }

    @Override
    public R<String> findTxtValueByTxtName() {
        return R.timeout();
    }

    @Override
    public R<String> generateConfigQrCode(GenerateConfigQrCodeForm generateConfigQrCodeForm) {
        return R.timeout();
    }

    @Override
    public R<String> sendTemplateMessage(SendTemplateMessageForm sendTemplateMessageForm) {
        return R.timeout();
    }

    @Override
    public R<Boolean> sendKefuMsg(SendKefuMsgForm paramSendKefuMsgForm) {
        return R.timeout();
    }

    @Override
    public R<WxJsapiSignature> signature(SignatureForm paramSignatureForm) {
        return R.timeout();
    }

    @Override
    public R<WxOAuth2UserInfo> oauth2getUserInfo(Oauth2getUserInfoForm paramOauth2getUserInfoForm) {
        return R.timeout();
    }

    @Override
    public R<WxOAuth2UserInfo> oauth2RefreshAccessToken(Oauth2getUserInfoForm paramOauth2getUserInfoForm) {
        return R.timeout();
    }

    @Override
    public R<WxMediaUploadResult> uploadTemporaryMatrial(UploadTemporaryMatrialForm paramUploadTemporaryMatrialForm) {
        return R.timeout();
    }

    @Override
    public R<String> downloadMatrial(DownloadMatrialForm downloadMatrialForm) {
        return R.timeout();
    }

    @Override
    public R<List<WxUserTag>> loadTagList(GeneralForm generalForm) {
        return R.timeout();
    }

    @Override
    public R<Void> bindUserTags(BindUserTagsForm bindUserTagsForm) {
        return R.timeout();
    }

    @Override
    public R<WxMpUser> getFollowerInfo(GetFollowerInfoForm getFollowerInfoForm) {
        return R.timeout();
    }

    @Override
    public R<QrCodeDto> createFollowerPermanentQrCode(CreateFollowerPermanentQrCode paramCreateFollowerPermanentQrCode) {
        return R.timeout();
    }

    @Override
    public R<String> createSilentAuthUrl(CreateSilentAuthUrlForm createSilentAuthUrlForm) {
        return R.timeout();
    }

    @Override
    public R<Object> slientAuthLoginCheck(SlientAuthLoginCheckForm slientAuthLoginCheckForm) {
        return R.timeout();
    }

    @Override
    public R<String> varifyTokenForVerifyDomainName(VarifyTokenForVerifyDomainNameForm paramVarifyTokenForVerifyDomainNameForm) {
        return R.timeout();
    }

    @Override
    public R<Void> deletePubno(Config config) {
        return R.timeout();
    }

    @Override
    public R<Void> saveNotCheck(Config config) {
        return R.timeout();
    }

    @Override
    public R<List<Config>> query(Config data) {
        return R.timeout();
    }

    @Override
    public R<Config> saveOrUpdate(ConfigSaveDTO model) {
        return R.timeout();
    }

    @Override
    public R<String> queryWxMenu(String appId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> publishMenu2Wx(PublishMenuDTO publishMenuDTO) {
        return R.timeout();
    }

    @Override
    public R<String> getWxUserOpenId(String appId, String code) {
        return R.timeout();
    }

    @Override
    public R<WxOAuth2UserInfo> getWxUser(String appId, String code) {
        return R.timeout();
    }

    @Override
    public R<String> mediaImgUpload(MultipartFile simpleFile) {
        return R.timeout();
    }

    @Override
    public R<String> mediaImgUpload(String fileName, String fileUrl) {
        return R.timeout();
    }

    @Override
    public R<WxMpMaterialUploadResult> materialFileUpload(UploadTemporaryMatrialForm material) {
        return R.timeout();
    }


    @Override
    public R<Boolean> deleteMedia(String mediaId) {
        return R.timeout();
    }

    @Override
    public R<WxMpMassSendResult> massMessageSend(MassMpNewsMessage massMpNewsMessage) {
        return R.timeout();
    }

    @Override
    public R<WxMpMassSendResult> massMessageSend(MassVideoMessage massVideoMessage) {
        return R.timeout();
    }

    @Override
    public R<WxMpMassSendResult> massOpenIdsMessageSend(WxMpMassOpenIdsMessage message) {
        return R.timeout();
    }

    @Override
    public R<WxMpMassSendResult> massTagMessageSend(WxMpMassTagMessage message) {
        return R.timeout();
    }

    @Override
    public R<WxMpMassSendResult> massMessagePreview(MassMessagePreview preview) {
        return R.timeout();
    }

    @Override
    public R<WeiXinUserInfo> getWeiXinUserInfo(String openId) {
        return R.timeout();
    }

    @Override
    public R<Map<NotificationTarget, Integer>> countWxTagUser(NotificationTarget notificationTarget) {
        return R.timeout();
    }

    @Override
    public R<String> queryWxTagUser(TagsEnum tagsEnum) {
        return R.timeout();
    }

    @Override
    public R<List<WxMpUser>> batchGetUserInfo(SyncUserUnionIdDTO userUnionIdDTO) {
        return R.timeout();
    }

    @Override
    public R<List<String>> checkFolderShareUrlExist(String url) {
        return R.timeout();
    }

    @Override
    public R<Map<String, String>> officialAccountMigration(OfficialAccountMigrationDTO officialAccountMigrationDTO) {
        return R.timeout();
    }


    @Override
    public R<Boolean> deleteWeiXinUserInfo(String openId) {
        return R.timeout();
    }
}
