package com.caring.sass.wx.service.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.oauth.api.WXCallBackApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.wx.constant.MenuType;
import com.caring.sass.wx.dto.config.BindUserTagsForm;
import com.caring.sass.wx.dto.enums.TagsEnum;
import com.caring.sass.wx.service.config.ConfigAdditionalService;
import com.caring.sass.wx.service.config.ConfigService;
import com.caring.sass.wx.service.guide.RegGuideService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 微信关注消息处理
 *
 * @author xinzh
 */
@Slf4j
@Component
public class SubscribeHandler implements WxMpMessageHandler {

    private final WXCallBackApi wxCallBackApi;

    private final RedisTemplate<String, String> redisTemplate;

    private final TenantApi tenantApi;

    private final RegGuideService regGuideService;

    private final ConfigService configService;

    public SubscribeHandler(WXCallBackApi wxCallBackApi, RedisTemplate<String, String> redisTemplate,
                            TenantApi tenantApi, RegGuideService regGuideService,
                            ConfigService configService) {
        this.wxCallBackApi = wxCallBackApi;
        this.redisTemplate = redisTemplate;
        this.tenantApi = tenantApi;
        this.regGuideService = regGuideService;
        this.configService = configService;
    }

    public static void main(String[] args) {
        String eventKey = "doctor_personal_1231231";
        String[] split = eventKey.split("_");
        System.out.println(split[3]);
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        try {
            String appId = wxMpService.getWxMpConfigStorage().getAppId();
            String eventKey = wxMessage.getEventKey();
            String fromUser = wxMessage.getFromUser();
            String unionId = wxMessage.getUnionId();
            Long createTimeL = wxMessage.getCreateTime();

            // 微信用户关注的事件，一定时间内只能推送过来处理一次
            String redisKey = createTimeL + fromUser;
            String createTime = redisTemplate.opsForValue().get(redisKey);

            if (StrUtil.isNotEmpty(createTime)) {
                // 上次缓存的时间， 小于当前时间。 则拒绝此处请求。
                long parseLong = Convert.toLong(createTime);
                long timeMillis = System.currentTimeMillis();
                if (timeMillis < parseLong) {
                    return null;
                }
                redisTemplate.delete(redisKey);
            }
            long timeMillis = System.currentTimeMillis() + 1000;
            redisTemplate.opsForValue().set(redisKey, timeMillis + "", 30000, TimeUnit.MILLISECONDS);
            redisTemplate.execute((RedisCallback<Object>) redisConnection -> redisConnection.setEx(redisKey.getBytes(), 60000, (timeMillis + "").getBytes()));

            WxSubscribeDto subscribeDto = new WxSubscribeDto();
            subscribeDto.setOpenId(fromUser);
            subscribeDto.setWxAppId(appId);
            subscribeDto.setUnionId(unionId);

            /**
             * 个人服务号项目。 微信粉丝关注过来，直接推送次登录图文
             */
            R<String> officialAccountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
            if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString().equals(officialAccountType.getData())) {
                R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
                Tenant tenant = tenantR.getData();
                if (tenant == null) {
                    log.error("PERSONAL_SERVICE_NUMBER tenant is null");
                    return null;
                }

                String url = ApplicationDomainUtil.apiUrl() + "/api/wx/router/anno/" + tenant.getCode() + "/" + MenuType.SELECT_ROLE.name();
                WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
                item.setTitle("欢迎关注" + tenant.getName());
                item.setDescription("点此注册登录");
                item.setPicUrl(tenant.getLogo());
                item.setUrl(url);
                subscribeDto.setLocale(Locale.SIMPLIFIED_CHINESE);
                WxMpXmlOutNewsMessage newsMessage = WxMpXmlOutMessage.NEWS()
                        .toUser(fromUser)
                        .fromUser(wxMessage.getToUser())
                        .build();
                newsMessage.setCreateTime(System.currentTimeMillis());
                newsMessage.setArticleCount(1);
                newsMessage.setMsgType("news");
                newsMessage.addArticle(item);
                log.error("PERSONAL_SERVICE_NUMBER newsMessage {}", newsMessage.toXml());
                return newsMessage;
            }

            log.info("SubscribeHandler handle {}" ,eventKey);
            if (eventKey.contains("doctor_personal_english_")) {
                String doctorId = "";
                String[] split = eventKey.split("_");
                doctorId = split[split.length -1];
                subscribeDto.setLocale(Locale.ENGLISH);
                subscribeDto.setDoctorId(Long.parseLong(doctorId));
                wxCallBackApi.subscribe(subscribeDto);
                return null;
            }
            // 患者通过医生的二维码关注
            if (eventKey.contains("doctor_personal_")) {
                String doctorId = "";
                String[] split = eventKey.split("_");
                doctorId = split[split.length -1];
                subscribeDto.setLocale(Locale.SIMPLIFIED_CHINESE);
                subscribeDto.setDoctorId(Long.parseLong(doctorId));
                wxCallBackApi.subscribe(subscribeDto);
                return null;
            }
            // 患者拉新
            if ((!StringUtils.isEmpty(eventKey)) && (eventKey.contains("patient_invitation_"))) {
                String inviterPatientId = "";
                String[] split = eventKey.split("_");
                if (split.length >= 3) {
                    inviterPatientId = split[3];
                }
                subscribeDto = new WxSubscribeDto();
                subscribeDto.setOpenId(wxMessage.getFromUser());
                subscribeDto.setUnionId(wxMessage.getUnionId());
                subscribeDto.setCreateTime(wxMessage.getCreateTime());
                subscribeDto.setWxAppId(appId);
                subscribeDto.setInviterPatientId(Long.parseLong(inviterPatientId));
                wxCallBackApi.subscribe(subscribeDto);
                return null;
            }

            // 医生关注
            if (eventKey.contains("doctor_login_")) {
                subscribeDto.setLocale(Locale.SIMPLIFIED_CHINESE);
                wxCallBackApi.doctorSubscribe(subscribeDto);
                return null;
            }

            if (eventKey.contains("doctor_english_login_")) {
                subscribeDto.setLocale(Locale.ENGLISH);
                wxCallBackApi.doctorSubscribe(subscribeDto);
                return null;
            }

            if (eventKey.contains("assistant_login_")) {
                subscribeDto.setLocale(Locale.SIMPLIFIED_CHINESE);
                wxCallBackApi.assistantSubscribe(subscribeDto);
                return null;
            }
            if (eventKey.contains("assistant_english_login_")) {
                subscribeDto.setLocale(Locale.ENGLISH);
                wxCallBackApi.assistantSubscribe(subscribeDto);
                return null;
            }

            if (eventKey.contains("qunfa_notification")) {
                wxCallBackApi.qunfaNotification(subscribeDto);
                return null;
            }

            // 普通会员关注
            if (StringUtils.isEmpty(eventKey)) {
                String tenantCode = BaseContextHandler.getTenant();
                String userDefaultRole = regGuideService.getWxUserDefaultRole(tenantCode);
                R<Tenant> apiByCode = tenantApi.getByCode(tenantCode);
                Tenant tenant = apiByCode.getData();
                subscribeDto.setLocale(Locale.SIMPLIFIED_CHINESE);
                if (Objects.nonNull(tenant)) {
                    String defaultLanguage = tenant.getDefaultLanguage();
                    if (StrUtil.isNotEmpty(defaultLanguage)) {
                        if (defaultLanguage.equals("en")) {
                            subscribeDto.setLocale(Locale.ENGLISH);
                        }
                    }
                }
                if (UserType.UCENTER_DOCTOR.equals(userDefaultRole)) {
                    // 默认角色设置为医生
                    wxCallBackApi.doctorSubscribe(subscribeDto);
                } else if (UserType.TOURISTS.equals(userDefaultRole)) {
                    // 判断患者 或者医生是否存在。
                    // 存在返回对应的角色
                    R<String> userExist = wxCallBackApi.wxUserExist(subscribeDto);
                    if (userExist.getIsSuccess() != null && userExist.getIsSuccess()) {
                        String userType = userExist.getData();
                        if (UserType.UCENTER_PATIENT.equals(userType)) {
                            wxCallBackApi.subscribe(subscribeDto);
                        } else if (UserType.UCENTER_DOCTOR.equals(userType)) {
                            wxCallBackApi.doctorSubscribe(subscribeDto);
                        } else {

                            // 不存在则设置为游客
                            // 给这个微信任务绑定 一个游客的标签
                            BindUserTagsForm bindUserTagsForm = new BindUserTagsForm();
                            bindUserTagsForm.setTagId(TagsEnum.TOURISTS_TAG.getValue());
                            bindUserTagsForm.setWxAppId(appId);
                            bindUserTagsForm.setOpenIds(fromUser);
                            List<String> clearTagIdList = new ArrayList<>();
                            clearTagIdList.add(TagsEnum.DOCTOR_TAGS.getValue());
                            clearTagIdList.add(TagsEnum.PATIENT_TAG.getValue());
                            bindUserTagsForm.setClearTagId(clearTagIdList);
                            configService.bindUserTags(bindUserTagsForm);

                            sendTouristRegister(subscribeDto, tenantCode);
                        }
                    }
                } else {
                    // 默认作为患者处理
                    wxCallBackApi.subscribe(subscribeDto);
                }
                return null;
            }
        } catch (Exception e) {
            log.error("关注出错了", e);
        }
        return null;
    }


    /**
     * 给游客发送一个注册的链接地址
     * @param subscribeDto
     * @param tenant
     */
    public void sendTouristRegister(WxSubscribeDto subscribeDto, String tenant) {
        SaasGlobalThreadPool.execute(() -> configService.sendTouristRegister(subscribeDto, tenant));
    }
}
