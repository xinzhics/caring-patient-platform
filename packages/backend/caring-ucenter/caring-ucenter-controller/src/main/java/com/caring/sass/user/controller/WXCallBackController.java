package com.caring.sass.user.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.UserType;

import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.constant.QrDoctorTypeEnum;
import com.caring.sass.user.dao.BulkNotificationScanUserMapper;
import com.caring.sass.user.dao.NursingStaffMapper;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.user.dto.wx.message.recv.ImageMessage;
import com.caring.sass.user.dto.wx.message.recv.TextMessage;
import com.caring.sass.user.dto.wx.message.recv.VideoMessage;
import com.caring.sass.user.dto.wx.message.recv.VoiceMessage;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.PatientRecommendRelationshipService;
import com.caring.sass.user.service.PatientRecommendSettingService;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.user.service.impl.NotificationSendServiceImpl;
import com.caring.sass.user.service.impl.WxMessageCallBackService;
import com.caring.sass.user.util.I18nUtils;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.BindUserTagsForm;
import com.caring.sass.wx.dto.config.GeneralForm;
import com.caring.sass.wx.dto.config.GetFollowerInfoForm;
import com.caring.sass.wx.dto.config.SendKefuMsgForm;
import com.caring.sass.wx.dto.enums.TagsEnum;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @ClassName WechatCallBackController
 * @Description
 * @Author yangShuai
 * @Date 2020/9/27 9:45
 * @Version 1.0
 */
@Slf4j
@ApiIgnore
@RestController
@RequestMapping("/wx/message/callBack")
public class WXCallBackController {

    @Autowired
    DoctorService doctorService;

    @Autowired
    NursingStaffMapper nursingStaffMapper;

    @Autowired
    PatientService patientService;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    WxMessageCallBackService wxMessageCallBackService;

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    BulkNotificationScanUserMapper bulkNotificationScanUserMapper;

    @Autowired
    NotificationSendServiceImpl notificationSendService;

    @Autowired
    PatientRecommendRelationshipService patientRecommendRelationshipService;
    @Autowired
    PatientRecommendSettingService patientRecommendSettingService;

    /**
     * 可以考虑修改成从redis中获取
     * @param wxAppId
     */
    public void setTenantCode(String wxAppId) {
        if (StrUtil.isNotEmpty(wxAppId)) {
            R<Tenant> tenant = tenantApi.getTenantByWxAppId(wxAppId);
            if (tenant.getIsError() || Objects.isNull(tenant.getData())) {
                throw new BizException("未查询到公众号对应的项目，无法处理业务需求");
            }
            BaseContextHandler.setTenant(tenant.getData().getCode());
        } else {
            String tenant = BaseContextHandler.getTenant();
            if (StrUtil.isEmpty(tenant)) {
                throw new BizException("未知的租户");
            }
        }
    }

    /**
     * @return com.caring.sass.base.R
     * @Author yangShuai
     * @Description 扫码关注。可能是患者。可能是医生误扫
     * @Date 2020/9/27 16:14
     */
    @PostMapping("patient/subscribe")
    public R<Boolean> subscribe(@RequestBody @Validated WxSubscribeDto wxSubscribeDto) {
        try {
            setTenantCode(wxSubscribeDto.getWxAppId());
            // 直接关注的。 确定这个人之前是不是医生
            // 检验这个关注的人的openId是不是本项目的里面医生
            Doctor doctor = doctorService.checkOpenId(wxSubscribeDto);
            if (Objects.nonNull(doctor)) {
                doctorService.subscribe(wxSubscribeDto.getWxAppId(), wxSubscribeDto.getOpenId(), doctor, wxSubscribeDto.getLocale());
                return R.success();
            }
            //患者拉新
            PatientRecommendSetting patientRecommendSetting = null;
            Boolean patientRecommendFlag = false;
            Patient inviterPatient = null;
            if (wxSubscribeDto.getDoctorId()==null  && wxSubscribeDto.getInviterPatientId()!=null){
                inviterPatient = patientService.getById(wxSubscribeDto.getInviterPatientId());
                patientRecommendFlag = true;
                LbqWrapper<PatientRecommendSetting> lbqWrapper = new LbqWrapper();
                patientRecommendSetting = patientRecommendSettingService.getOne(lbqWrapper);
                if (ObjectUtils.isEmpty(patientRecommendSetting)){
                    log.error("未设置患者推荐配置！");
                    return R.success();
                }else if (ObjectUtils.isEmpty(inviterPatient)){
                    log.error("邀请人患者信息不存！");
                    return R.success();
                }else if (localDateIsAfter(LocalDate.now(),patientRecommendSetting.getActivityEndDate())){
                    log.error("活动已过期！");
                }
                if (QrDoctorTypeEnum.SOURCE_DOCTOR.getCode()==patientRecommendSetting.getQrDoctorType()){
                    wxSubscribeDto.setDoctorId(inviterPatient.getDoctorId());
                }else {
                    wxSubscribeDto.setDoctorId(patientRecommendSetting.getDoctorId());
                }
                //患者已存在，则是取关偶，再次关注。
                LbqWrapper<Patient> queryWrapper = new LbqWrapper<>();
                queryWrapper.eq(Patient::getOpenId, wxSubscribeDto.getOpenId());
                Patient Patient =  patientService.getOne(queryWrapper);
                if (!ObjectUtils.isEmpty(Patient)){
                    patientRecommendFlag = false;
                    wxSubscribeDto.setDoctorId(Patient.getDoctorId());
                }
            }

            Patient subscribe = patientService.subscribe(wxSubscribeDto);

            // 异步给患者添加 微信的患者表标签
            patientService.createWeiXinTag(subscribe, BaseContextHandler.getTenant());
            // 创建患者推荐关系
            if (patientRecommendFlag && !ObjectUtils.isEmpty(patientRecommendSetting)){
                patientRecommendRelationshipService.createPatientRecommendRelationship(inviterPatient,subscribe,wxSubscribeDto.getCreateTime(),patientRecommendSetting, BaseContextHandler.getTenant());
            }
            return R.success();
        } catch (Exception e) {
            log.error("关注业务失败", e);
            return R.fail("关注业务失败");
        }
    }
    /**
     * 比较第一个日期是否大于第二个日期
     * @param firstDate 第一个日期
     * @param secondDate 第二个日期
     * @return true-大于;false-不大于
     */
    private boolean localDateIsAfter(LocalDate firstDate, LocalDate secondDate) {
        return firstDate.isAfter(secondDate);
    }
    /**
     * @return com.caring.sass.base.R
     * @Author yangShuai
     * @Description 医生关注
     * @Date 2020/9/27 16:14
     */
    @PostMapping("doctor/subscribe")
    public R<Boolean> doctorSubscribe(@RequestBody @Validated WxSubscribeDto query) {
        setTenantCode(query.getWxAppId());
        boolean patientExist = patientService.checkOpenId(query.getOpenId(), "checkIsDoctor");
        if (!patientExist) {
            doctorService.subscribe(query.getWxAppId(), query.getOpenId(), null, query.getLocale());
        } else {
            doctorService.subscribeChangeDoctor(query.getWxAppId(), query.getOpenId(), query.getLocale());
        }

        return R.success();
    }

    @ApiOperation("医助扫激活码登录")
    @PostMapping("assistant/subscribe")
    public R<Boolean> assistantSubscribe(@RequestBody @Validated WxSubscribeDto query) {
        setTenantCode(query.getWxAppId());
        WxMpKefuMessage.WxArticle article1 = new WxMpKefuMessage.WxArticle();
        // 查询openId是否已经有医助。
        List<NursingStaff> nursingStaffs = nursingStaffMapper.selectList(Wraps.<NursingStaff>lbQ().eq(NursingStaff::getOpenId, query.getOpenId()));
        if (CollUtil.isNotEmpty(nursingStaffs)) {
            for (NursingStaff staff : nursingStaffs) {
                staff.setWxStatus(1);
                nursingStaffMapper.updateById(staff);
            }
        }
        R<Tenant> tenant = tenantApi.getTenantByWxAppId(query.getWxAppId());
        if (tenant.getIsSuccess()) {
            Tenant tenantData = tenant.getData();

            BindUserTagsForm bindUserTagsForm = new BindUserTagsForm();
            bindUserTagsForm.setWxAppId(query.getWxAppId());
            bindUserTagsForm.setOpenIds(query.getOpenId());
            bindUserTagsForm.setTagId(TagsEnum.ASSISTANT_TAG.getValue());
            List<String> stringList = new ArrayList<>();
            bindUserTagsForm.setClearTagId(stringList);
            weiXinApi.bindUserTags(bindUserTagsForm);

            GeneralForm generalForm = new GeneralForm();
            generalForm.setWxAppId(query.getWxAppId());
            String url = ApplicationDomainUtil.wxAssistantBaseDomain(tenantData.getDomainName(), Objects.nonNull(tenantData.getWxBindTime()));
            DictionaryItemService itemService = SpringUtils.getBean(DictionaryItemService.class);
            String assistantName = "医助";
            if (itemService != null) {
                List<DictionaryItem> dictionaryItems = itemService.list(Wraps.lbQ());
                Map<String, String> dictionaryMap = new HashMap<>();
                for (DictionaryItem item : dictionaryItems) {
                    dictionaryMap.put(item.getCode(), item.getName());
                }
                String assistant = dictionaryMap.get("assistant");
                if (StrUtil.isNotEmpty(assistant)) {
                    assistantName = assistant;
                }
            }
            article1.setUrl(url);
            article1.setPicUrl(tenantData.getLogo());
            article1.setDescription(I18nUtils.getMessage("CLICK_TO_LOGIN", query.getLocale()));
            article1.setTitle(I18nUtils.getMessage("Welcome", query.getLocale(), assistantName));
            WxMpKefuMessage wxMpKefuMessage = (WxMpKefuMessage.NEWS().toUser(query.getOpenId())).addArticle(article1).build();

            SendKefuMsgForm form = new SendKefuMsgForm();
            form.setWxAppId(query.getWxAppId());
            form.setMessage(wxMpKefuMessage);
            weiXinApi.sendKefuMsg(form);
        }
        return R.success();
    }

    @ApiOperation("微信用户是否在系统中存在")
    @PostMapping("wxUserExist")
    public R<String> wxUserExist(@RequestBody @Validated WxSubscribeDto query) {
        setTenantCode(query.getWxAppId());
        boolean patientExist = patientService.checkOpenId(query.getOpenId(), "checkUserExist");
        if (patientExist) {
            return R.success(UserType.UCENTER_PATIENT);
        } else {
            Doctor doctor = doctorService.checkOpenId(query);
            if (Objects.nonNull(doctor)) {
                return R.success(UserType.UCENTER_DOCTOR);
            }
        }
        return R.success(UserType.TOURISTS);
    }


    @ApiOperation("个人服务号用户关注事件")
    @PostMapping("personalServiceNumber/subscribe")
    public void personalServiceNumberSubscribe(@RequestBody WxSubscribeDto subscribeDto) {

        String openId = subscribeDto.getOpenId();
        Patient patient = patientService.findByOpenId(openId);
        if (Objects.nonNull(patient)) {
            if (patient.getIsCompleteEnterGroup() == 1) {
                patient.setStatus(Patient.PATIENT_SUBSCRIBE_NORMAL);
            } else {
                patient.setStatus(Patient.PATIENT_SUBSCRIBE_NORMAL);
            }
            patientService.updateById(patient);
        }
        Doctor doctor = doctorService.findByOpenId(openId);
        if (Objects.nonNull(doctor)) {
            doctor.setWxStatus(1);
            doctorService.updateById(doctor);
        }
        List<NursingStaff> nursingStaffs = nursingStaffMapper.selectList(Wraps.<NursingStaff>lbQ().eq(NursingStaff::getOpenId, openId));
        if (CollUtil.isNotEmpty(nursingStaffs)) {
            for (NursingStaff staff : nursingStaffs) {
                NursingStaff staff1 = new NursingStaff();
                staff1.setId(staff.getId());
                staff1.setWxStatus(1);
                nursingStaffMapper.updateById(staff1);
            }
        }

    };

    /**
     * 群发通知的扫描或者关注
     * @param query
     * @return
     */
    @PostMapping("qunfa/notification")
    public R<Boolean> qunfaNotification(@RequestBody @Validated WxSubscribeDto query) {

        String openId = query.getOpenId();
        String wxAppId = query.getWxAppId();
        Integer count = bulkNotificationScanUserMapper.selectCount(Wraps.<BulkNotificationScanUser>lbQ().eq(BulkNotificationScanUser::getOpenId, openId));
        if (count != null && count > 0) {
            return R.success(true);
        }
        GetFollowerInfoForm form = new GetFollowerInfoForm();
        form.setWxAppId(wxAppId);
        form.setUserOpenId(openId);

        R<WxMpUser> wxMpUserR = weiXinApi.getFollowerInfo(form);
        if (wxMpUserR.getIsSuccess() != null && wxMpUserR.getIsSuccess()) {
            WxMpUser data = wxMpUserR.getData();
            String nickname = data.getNickname();
            BulkNotificationScanUser scanUser = BulkNotificationScanUser.builder()
                    .openId(openId)
                    .nickname(nickname)
                    .wxAppId(wxAppId)
                    .build();
            bulkNotificationScanUserMapper.insert(scanUser);
        }
        return R.success(true);
    }

    /**
     * @return com.caring.sass.base.R
     * @Author yangShuai
     * @Description 图文消息处理
     * @Date 2020/9/27 17:16
     */
    @PostMapping({"onReceiveImageMessageEvent"})
    public R<Boolean> onReceiveImageMessageEvent(@RequestBody ImageMessage imageMessage) {
        wxMessageCallBackService.onReceiveImageMessageEvent(imageMessage);
        return R.success();
    }


    /**
     * @return com.caring.sass.base.R
     * @Author yangShuai
     * @Description 微信回调的 文字消息
     * @Date 2020/9/27 16:37
     */
    @PostMapping({"onReceiveTextMessageEvent"})
    public R<Boolean> onReceiveTextMessageEvent(@RequestBody TextMessage textMessage) {

        // 检查是否是一个 #111111 此格式的消息
        String content = textMessage.getContent();
        String pattern = "^#\\d{6}";
        String trim = content.trim();
        if (StringUtils.isNotEmptyString(trim)) {
            boolean matches = Pattern.matches(pattern, trim);
            if (matches) {
                // 检查发送人是否是个一个群发通知的扫码人员
                String openId = textMessage.getFromUserName();
                List<BulkNotificationScanUser> scanUsers = bulkNotificationScanUserMapper.selectList(Wraps.<BulkNotificationScanUser>lbQ()
                        .eq(BulkNotificationScanUser::getOpenId, openId));
                if (CollUtil.isNotEmpty(scanUsers)) {
                    // 将content中的code分解出来。查询 群发通知的模板设置， 并这这个用户推送 对应的模板
                    String code = trim.substring(1, 7);
                    BulkNotificationScanUser scanUser = scanUsers.get(0);
                    notificationSendService.sendTemplateMsg(code, scanUser);
                    return R.success(true);
                }
            }
        }
        wxMessageCallBackService.onReceiveTextMessageEvent(textMessage);
        return R.success();
    }

    /**
     * @return com.caring.sass.base.R
     * @Author yangShuai
     * @Description 取消关注消息处理
     * @Date 2020/9/27 17:16
     */
    @GetMapping("unsubscribe")
    public R<Boolean> unsubscribe(@RequestParam(value = "openId") String openId) {
        wxMessageCallBackService.unsubscribe(openId);
        return R.success();
    }

    @PostMapping("onReceiveVideoMessageEvent")
    public R<Boolean> onReceiveVideoMessageEvent(VideoMessage message) {
        wxMessageCallBackService.onReceiveVideoMessageEvent(message);
        return R.success();
    }

    @PostMapping("onReceiveVoiceMessageEvent")
    public R<Boolean> onReceiveVoiceMessageEvent(VoiceMessage message) {
        wxMessageCallBackService.onReceiveVoiceMessageEvent(message);
        return R.success();
    }

}



