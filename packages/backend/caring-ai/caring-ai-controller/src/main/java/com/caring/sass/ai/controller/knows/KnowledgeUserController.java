package com.caring.sass.ai.controller.knows;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.ai.article.service.AiUserService;
import com.caring.sass.ai.article.service.ArticleUserService;
import com.caring.sass.ai.card.service.BusinessCardService;
import com.caring.sass.ai.dto.ArticleUserSaveDTO;
import com.caring.sass.ai.dto.UserResetPasswordCodeDTO;
import com.caring.sass.ai.dto.UserResetPasswordDTO;
import com.caring.sass.ai.dto.UserUpdatePasswordDTO;
import com.caring.sass.ai.dto.aggregation.KnowUserAggregationInfo;
import com.caring.sass.ai.dto.know.*;
import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.ai.know.config.KnowChildDomainConfig;
import com.caring.sass.ai.know.config.KnowWxPayConfig;
import com.caring.sass.ai.know.dao.KnowledgeUserDomainMapper;
import com.caring.sass.ai.know.service.*;
import com.caring.sass.ai.service.SmsSendService;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.ai.utils.I18nUtils;
import com.caring.sass.ai.utils.PasswordUtils;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.OauthApi;
import com.caring.sass.sms.dto.SmsDto;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 知识库用户
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeUser")
@Api(value = "KnowledgeUser", tags = "知识库-知识库用户")
public class KnowledgeUserController {


    @Autowired
    KnowledgeUserService baseService;

    @Autowired
    OauthApi oauthApi;

    @Autowired
    KnowChildDomainConfig knowChildDomainConfig;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    KnowledgeFileService knowledgeFileService;

    @Autowired
    KnowledgeDailyCollectionService knowledgeDailyCollectionService;

    @Autowired
    SmsSendService smsSendService;

    @Autowired
    KnowWxPayConfig knowWxPayConfig;

    @Autowired
    BusinessCardService businessCardService;

    @Autowired
    ArticleUserService articleUserService;

    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;

    @Autowired
    KnowledgeMenuDomainService knowledgeMenuDomainService;

    @Autowired
    KnowledgeUserDomainMapper knowledgeUserDomainMapper;

    @Autowired
    FileUploadApi fileUploadApi;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    AiUserService aiUserService;

    @Autowired
    KnowledgeUserQualificationService qualificationService;

    @GetMapping("count/{domain}")
    @ApiOperation("统计文献")
    public R<KnowledgeFileCount> count(@PathVariable("domain") String domain) {

        Long userId;
        KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .eq(KnowledgeUser::getUserDomain, domain)
                .last(" limit 1"));
        if (Objects.isNull(user)) {
            return R.success(new KnowledgeFileCount());
        }
        userId = user.getId();

        KnowledgeFileCount fileCount = new KnowledgeFileCount();

        // 统计用的专业学术资料
        int count = knowledgeFileService.count(Wraps.<KnowledgeFile>lbQ()
                .eq(KnowledgeFile::getFileUserId, userId)
                .eq(KnowledgeFile::getKnowType, KnowledgeType.ACADEMIC_MATERIALS));
        fileCount.setZhuanYeXueShuCount(count);


        // 统计病例库
        int CASE_DATABASE = knowledgeFileService.count(Wraps.<KnowledgeFile>lbQ()
                .eq(KnowledgeFile::getKnowType, KnowledgeType.CASE_DATABASE)
                .eq(KnowledgeFile::getFileUserId, userId));

        fileCount.setBingLiKuCount(CASE_DATABASE);

        // 统计个人成果
        int PERSONAL_ACHIEVEMENTS = knowledgeFileService.count(Wraps.<KnowledgeFile>lbQ()
                .eq(KnowledgeFile::getKnowType, KnowledgeType.PERSONAL_ACHIEVEMENTS)
                .eq(KnowledgeFile::getFileUserId, userId));
        fileCount.setGeRenChengGuoCount(PERSONAL_ACHIEVEMENTS);

        // 统计日常收集
        int DAILY_COLLECTION = knowledgeDailyCollectionService.count(Wraps.<KnowledgeDailyCollection>lbQ()
                .eq(KnowledgeDailyCollection::getUserId, userId));
        fileCount.setRiChangShouJICount(DAILY_COLLECTION);

        return R.success(fileCount);

    }

    @ApiOperation("docKnowPay手机授权")
    @GetMapping("anno/redirectDocKnowPayWxAuthUrl")
    public void redirectDocKnowPayWxAuthUrl(HttpServletResponse resp,
                                            @RequestParam String domain,
                                            @RequestParam(required = false) String webSubDomain,
                                            @RequestParam(required = false) String webPrimaryDomain) {

        String wxAuthUrl = baseService.redirectDocKnowPayWxAuthUrl(domain, webSubDomain, webPrimaryDomain);
        try {
            resp.sendRedirect(wxAuthUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("手机端自动重定向授权")
    @GetMapping("anno/redirectMobileWxAuthUrl")
    public void redirectWxAuthUrl(HttpServletResponse resp, @RequestParam String domain) {

        String wxAuthUrl = baseService.createWxAuthUrl(domain);
        try {
            resp.sendRedirect(wxAuthUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @ApiOperation("二维码重定向授权")
    @GetMapping("anno/redirect/{domain}")
    public void createWxAuthUrl(
            HttpServletRequest req, HttpServletResponse resp,
            @PathVariable String domain) {

        String wxAuthUrl = baseService.createWxAuthUrl(domain);
        try {
            resp.sendRedirect(wxAuthUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @ApiOperation("创建网站的手机端分享链接")
    @GetMapping("anno/WxAuthUrl")
    public R<String> createWxAuthUrl(@RequestParam String domain) {

        String wxAuthUrl = baseService.createWxAuthUrl(domain);
        return R.success(wxAuthUrl);

    }



    @ApiOperation("查询openId用户是否存在")
    @GetMapping("anno/queryWxUser")
    public R<KnowledgeUser> queryWxUser(@RequestParam String openId, @RequestParam String domain) throws Exception {

        KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getOpenId, openId)
                .last(" limit 1 "));
        if (Objects.nonNull(user) && StrUtil.isNotEmpty(user.getUserMobile())) {
            user.setUserMobile(maskPhoneNumber(user.getUserMobile()));
        }
        return R.success(user);
    }


    /**
     * 检查 密码 或者验证码是否正确
     * @param knowledgeUserSaveDTO
     * @return
     */
    public KnowledgeUser checkUserPasswordOrCode(KnowledgeUserSaveDTO knowledgeUserSaveDTO) {
        String userMobile = knowledgeUserSaveDTO.getUserMobile();
        String password = knowledgeUserSaveDTO.getPassword();
        String smsCode = knowledgeUserSaveDTO.getSmsCode();
        userMobile = userMobile.trim();
        KnowledgeUser user = null;
        try {
            user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(userMobile))
                    .orderByAsc(SuperEntity::getCreateTime)
                    .last(" limit 0,1 "));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (StrUtil.isNotBlank(password)) {
            if (Objects.isNull(user)) {
                throw new BizException(I18nUtils.getMessage("user_not_exist_use_code_register"));
            }
            if (StrUtil.isBlank(user.getPassword())) {
                throw new BizException(I18nUtils.getMessage("password_not_set"));
            }
            if (!SecureUtil.md5(password).equals(user.getPassword())) {
                throw new BizException(I18nUtils.getMessage("password_error"));
            }
        } else {
            // 验证短信验证码是否正确
            checkSMS(userMobile, smsCode, true);
        }
        return user;
    }

    @ApiOperation("使用openId和手机号绑定，并授权")
    @PostMapping("anno/aiOpenIdMobileLogin")
    public R<JSONObject> aiOpenIdMobileLogin(@RequestBody @Validated KnowledgeUserSaveDTO knowledgeUserSaveDTO) throws Exception {

        String userMobile = knowledgeUserSaveDTO.getUserMobile();
        String openId = knowledgeUserSaveDTO.getOpenId();
        userMobile = userMobile.trim();
        KnowledgeUser user = checkUserPasswordOrCode(knowledgeUserSaveDTO);

        R<JSONObject> token = null;
        if (Objects.isNull(user)) {
            user = new KnowledgeUser();
            user.setUserMobile(userMobile);
            user.setOpenId(openId);
            user.setUserType(KnowDoctorType.GENERAL_PRACTITIONER);
            user.setMembershipLevel(MembershipLevel.FREE_VERSION);
            baseService.save(user);
        } else {
            KnowledgeUser openIdUser = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getOpenId, openId)
                    .last(" limit 1 "));
            if (openIdUser != null) {
                openIdUser.setOpenId(null);
                baseService.updateAllById(openIdUser);
            }
            user.setOpenId(openId);
            baseService.updateById(user);
        }

        token = oauthApi.createTempToken(user.getId());

        token.getData().put("userType", user.getUserType());
        token.getData().put("userAvatar", user.getUserAvatar());
        token.getData().put("userName", user.getUserName());
        token.getData().put("userMobile", maskPhoneNumber(userMobile));
        return token;
    }


    @ApiOperation("使用openId登录")
    @GetMapping("anno/useOpenIdLogin")
    public R<JSONObject> useOpenIdLogin(@RequestParam String openId, @RequestParam(required = false) String domain) {

        KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getOpenId, openId)
                .last(" limit 1 "));
        if (Objects.isNull(user)) {
            throw new BizException("用户不存在");
        }
        R<JSONObject> token = null;
        token = oauthApi.createTempToken(user.getId());
        try {
            if (StrUtil.isNotEmpty(user.getUserMobile())) {
                token.getData().put("userMobile", maskPhoneNumber(user.getUserMobile()));
            }
        } catch (Exception e) {
        }
        token.getData().put("userType", user.getUserType());
        token.getData().put("userAvatar", user.getUserAvatar());
        token.getData().put("userName", user.getUserName());
        return token;
    }


    /**
     * 获取前端用户发起请求时的 二级域名
     * @param request
     * @return
     */
    private String getRequestDomain(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        String tempDomain = "";
        String replace = referer.replace("https://", "");
        String[] split = replace.split("\\.");
        if (split.length < 3) {
            tempDomain = knowChildDomainConfig.getDocuknowMainDoamin();
        } else {
            tempDomain = split[0];
        }
        return tempDomain;
    }

    @ApiOperation("查询博主知识库和名片聚合信息")
    @GetMapping("/anno/getAggregationInfo")
    public R<KnowUserAggregationInfo> getAggregationInfo(
            HttpServletRequest request,
            @RequestParam String doMain) throws Exception {

        KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                .select(KnowledgeUser::getUserName,
                        KnowledgeUser::getUserMobile,
                        SuperEntity::getId,
                        KnowledgeUser::getDigitalStudioUrl,
                        KnowledgeUser::getOtherUrl,
                        KnowledgeUser::getUserAvatar,
                        KnowledgeUser::getSharePoster,
                        KnowledgeUser::getUserSetUiStyle,
                        KnowledgeUser::getBasicMembershipPrice,
                        KnowledgeUser::getAiStudioUrl,
                        KnowledgeUser::getAiStudioDoctorId,
                        KnowledgeUser::getAiInteractiveAudioTrialListening,
                        KnowledgeUser::getPersonalProfile,
                        KnowledgeUser::getWorkUnit,
                        KnowledgeUser::getDepartment,
                        KnowledgeUser::getDoctorTitle,
                        KnowledgeUser::getGreetingVideo,
                        KnowledgeUser::getSubscribeUserName,
                        KnowledgeUser::getGreetingVideoCover,
                        KnowledgeUser::getRealHumanAvatar,
                        KnowledgeUser::getSpecialty,
                        KnowledgeUser::getSubscribeSwitch,
                        KnowledgeUser::getSubscribeLastUpdateTime,
                        KnowledgeUser::getOpenAcademicMaterials,
                        KnowledgeUser::getOpenMonthlyPayment,
                        KnowledgeUser::getOpenAnnualPayment,
                        KnowledgeUser::getOpenCaseDatabase,
                        KnowledgeUser::getOpenDailyCollection,
                        KnowledgeUser::getOpenPersonalAchievements,
                        KnowledgeUser::getOpenArticleData,
                        KnowledgeUser::getOpenTextual,
                        KnowledgeUser::getDataSource,
                        KnowledgeUser::getChildDomain,
                        KnowledgeUser::getThirdPartyUserId,
                        KnowledgeUser::getThirdPartyKnowledgeLink,
                        KnowledgeUser::getUploadQualificationRemindTime,
                        KnowledgeUser::getCallSwitch,
                        KnowledgeUser::getTotalCallDuration,
                        KnowledgeUser::getProfessionalVersionPrice)
                .eq(KnowledgeUser::getUserDomain, doMain)
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .last(" limit 1 "));

        if (Objects.isNull(user)) {
            return R.success(null);
        }

        if (user.getBasicMembershipPrice() == null || user.getProfessionalVersionPrice() == null) {
            user.setBasicMembershipPrice(knowWxPayConfig.getBasicMembershipPrice(null));
            user.setProfessionalVersionPrice(knowWxPayConfig.getProfessionalVersionPrice(null));
        }
        if (user.getUserName() == null) {
            user.setUserName("用户"+ user.getUserMobile().substring(user.getUserMobile().length() -4));
        }

        List<KnowledgeUserQualification> qualifications = qualificationService.list(Wraps.<KnowledgeUserQualification>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(KnowledgeUserQualification::getKnowUserId, user.getId()));
        user.setUserQualification(qualifications);

        KnowUserAggregationInfo knowUserAggregationInfo = new KnowUserAggregationInfo();
        knowUserAggregationInfo.setKnowledgeUser(user);
        knowUserAggregationInfo.setBusinessCard(businessCardService.getOne(Wraps.<BusinessCard>lbQ()
                .eq(BusinessCard::getPhone, EncryptionUtil.encrypt(user.getUserMobile()))
                .last(" limit 0 , 1 ")));


        ArticleUser articleUser = articleUserService.getOne(Wraps.<ArticleUser>lbQ()
                .eq(ArticleUser::getUserMobile, EncryptionUtil.encrypt(user.getUserMobile()))
                .last(" limit 0 , 1 "));
        if (Objects.nonNull(articleUser)) {
            knowUserAggregationInfo.setArticleUserId(articleUser.getId());
        }
        TextualInterpretationUser textualInterpretationUser = textualInterpretationUserService.getOne(Wraps.<TextualInterpretationUser>lbQ()
                .eq(TextualInterpretationUser::getUserMobile, EncryptionUtil.encrypt(user.getUserMobile()))
                .last(" limit 0 , 1 "));
        if (Objects.nonNull(textualInterpretationUser)) {
            knowUserAggregationInfo.setTextualInterpretationUserId(textualInterpretationUser.getId());
        }
        return R.success(knowUserAggregationInfo);
    }




    @ApiOperation("查询网站博主的姓名和头像")
    @GetMapping("/anno/queryAdminUserInfo")
    public R<KnowledgeUser> queryAdminUserInfo(@RequestParam String doMain) {

        KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                        .select(KnowledgeUser::getUserName, KnowledgeUser::getUserMobile,
                                SuperEntity::getId,
                                KnowledgeUser::getDigitalStudioUrl,
                                KnowledgeUser::getOtherUrl,
                                KnowledgeUser::getUserAvatar,
                                KnowledgeUser::getSharePoster,
                                KnowledgeUser::getUserSetUiStyle,
                                KnowledgeUser::getBasicMembershipPrice,
                                KnowledgeUser::getAiStudioUrl,
                                KnowledgeUser::getSubscribeSwitch,
                                KnowledgeUser::getSubscribeUserName,
                                KnowledgeUser::getSubscribeLastUpdateTime,
                                KnowledgeUser::getOpenAcademicMaterials,
                                KnowledgeUser::getOpenMonthlyPayment,
                                KnowledgeUser::getOpenAnnualPayment,
                                KnowledgeUser::getOpenCaseDatabase,
                                KnowledgeUser::getOpenDailyCollection,
                                KnowledgeUser::getOpenPersonalAchievements,
                                KnowledgeUser::getOpenArticleData,
                                KnowledgeUser::getOpenTextual,
                                KnowledgeUser::getDataSource,
                                KnowledgeUser::getThirdPartyUserId,
                                KnowledgeUser::getThirdPartyKnowledgeLink,
                                KnowledgeUser::getProfessionalVersionPrice)
                .eq(KnowledgeUser::getUserDomain, doMain)
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .last(" limit 1 "));

        if (user.getBasicMembershipPrice() == null || user.getProfessionalVersionPrice() == null) {
            user.setBasicMembershipPrice(knowWxPayConfig.getBasicMembershipPrice(null));
            user.setProfessionalVersionPrice(knowWxPayConfig.getProfessionalVersionPrice(null));
        }
        if (user.getUserName() == null) {
            user.setUserName("用户"+ user.getUserMobile().substring(user.getUserMobile().length() -4));
        }
        return R.success(user);
    }

    @ApiOperation("更新资质提醒时间")
    @PutMapping("/UploadQualificationRemindTime")
    public R<Boolean> UploadQualificationRemindTime(@RequestParam Long userId) {

        KnowledgeUser knowledgeUser = new KnowledgeUser();
        knowledgeUser.setId(userId);
        knowledgeUser.setUploadQualificationRemindTime(LocalDateTime.now());
        baseService.updateById(knowledgeUser);
        return R.success(true);
    }

    @ApiOperation("更新博主的网站风格")
    @PutMapping("/updateUserUiStyle")
    public R<Boolean> updateUserUiStyle(@RequestParam Long userId, @RequestParam Integer userSetUiStyle) {

        KnowledgeUser knowledgeUser = new KnowledgeUser();
        knowledgeUser.setId(userId);
        knowledgeUser.setUserSetUiStyle(userSetUiStyle);
        baseService.updateById(knowledgeUser);
        return R.success(true);
    }



    @ApiOperation("使用用户ID和refreshToken更新token")
    @PostMapping("anno/refreshToken")
    public R<JSONObject> refreshToken(@RequestBody KnowledgeUserUpdateDTO userUpdateDTO) {

        Long userId = userUpdateDTO.getUserId();
        KnowledgeUser user = baseService.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        String refreshToken = userUpdateDTO.getRefreshToken();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("refreshToken", refreshToken);
        return oauthApi.aiAuthRefreshToken(jsonObject);
    }




    @ApiOperation("初始化海报")
    @PostMapping("initMergePaper")
    public R<Boolean> initMergePaper() {

        baseService.initMergePaper();
        return R.success(true);
    }



    @ApiOperation("上传系统文档并同步到所有博主")
    @PostMapping("uploadSystemFile")
    public R<String> uploadSystemFile(@RequestBody @Validated KnowledgeSystemFile systemFile) {

//        baseService.uploadSystemFile(systemFile);
        return R.success("SUCCESS");
    }


    @Deprecated
    @ApiOperation("初始化博主的系统文章")
    @GetMapping("initUserSystemFile")
    public R<String> initUserSystemFile() {

        baseService.initUserSystemFile();
        return R.success("SUCCESS");
    }


    @ApiOperation("更新当前用户的名称头像职称科室")
    @PutMapping("updateKnowledgeUser")
    public R<KnowledgeUser> updateKnowledgeUser(@RequestBody @Validated KnowledgeUser knowledgeUser) {
        Long userId = knowledgeUser.getId();
        String userName = knowledgeUser.getUserName();
        KnowledgeUser user = new KnowledgeUser();
        user.setId(userId);
        user.setUserName(userName);
        user.setUserAvatar(knowledgeUser.getUserAvatar());
        user.setWorkUnit(knowledgeUser.getWorkUnit());
        user.setDepartment(knowledgeUser.getDepartment());
        user.setDoctorTitle(knowledgeUser.getDoctorTitle());
        user.setPersonalProfile(knowledgeUser.getPersonalProfile());
        baseService.updateUserName(user);
        return R.success(user);
    }



    @ApiOperation("手机号用户是否存在")
    @PostMapping("anno/countUserMobile")
    public R<KnowledgeUser> countUserMobile(@RequestBody @Validated KnowledgeUserExistDTO userMobile) {

        String userDomain = userMobile.getUserDomain();
        String mobile = userMobile.getUserMobile();

        LbqWrapper<KnowledgeUser> lbqWrapper = null;
        try {
            lbqWrapper = Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(mobile))
                    .in(KnowledgeUser::getUserType, KnowDoctorType.MINI_USER, KnowDoctorType.CHIEF_PHYSICIAN)
                    .last(" limit 0,1 ");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (StrUtil.isNotEmpty(userDomain)) {
            lbqWrapper.eq(KnowledgeUser::getUserDomain, userDomain);
        }

        KnowledgeUser user = null;
        try {
            user = baseService.getOne(lbqWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 由于点记小程序要放开，所以用户这里设计成 账号不存在，就生成一个知识库方面的小程序角色账号。
        if (user == null) {
            user = new KnowledgeUser();
            user.setUserMobile(mobile);
            user.setUserType(KnowDoctorType.MINI_USER);
            user.setUserName("用户"+ mobile.substring(mobile.length() -4));
            user.setMembershipLevel(MembershipLevel.FREE_VERSION);
            baseService.save(user);
        }

        return R.success(user);
    }

    @Deprecated
    @GetMapping("mySubscribe")
    @ApiOperation(value = "我订阅的博主列表")
    public R<List<KnowledgeUser>> mySubscribe(@RequestParam Long userId ) {

        List<KnowledgeUserSubscribe> userSubscribes = baseService.subscribeList(Wraps.<KnowledgeUserSubscribe>lbQ()
                .orderByDesc(KnowledgeUserSubscribe::getSubscribeTime)
                .eq(KnowledgeUserSubscribe::getKnowledgeUserId, userId));
        if (CollUtil.isEmpty(userSubscribes)) {
            return R.success(new ArrayList<>());
        }
        List<String> damainList = userSubscribes.stream().map(KnowledgeUserSubscribe::getUserDomain).collect(Collectors.toList());
        List<KnowledgeUser> knowledgeUsers = baseService.list(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .in(KnowledgeUser::getUserDomain, damainList));
        if (CollUtil.isEmpty(knowledgeUsers)) {
            return R.success(new ArrayList<>());
        }
        Map<String, KnowledgeUser> knowledgeUserMap = knowledgeUsers.stream()
                .collect(Collectors.toMap(KnowledgeUser::getUserDomain, knowledgeUser -> knowledgeUser, (o1, o2) -> o1));

        List<KnowledgeUser> knowledgeUserList = new ArrayList<>();
        for (KnowledgeUserSubscribe userSubscribe : userSubscribes) {
            KnowledgeUser knowledgeUser = knowledgeUserMap.get(userSubscribe.getUserDomain());
            knowledgeUser.setMembershipLevel(userSubscribe.getMembershipLevel());
            knowledgeUser.setMembershipExpiration(userSubscribe.getMembershipExpiration());
            knowledgeUser.setExperienceAiTime(userSubscribe.getExperienceAiTime());
            knowledgeUser.setSubscribeTime(userSubscribe.getSubscribeTime());
            knowledgeUserList.add(knowledgeUser);
        }
        return R.success(knowledgeUserList);

    }





    @Deprecated
    @PostMapping("customPage")
    @ApiOperation(value = "查询订阅我的会员列表")
    public R<IPage<KnowledgeUser>> customPage(@RequestBody PageParams<KnowledgeUserPageDTO> params) {

        IPage<KnowledgeUser> builtPage = params.buildPage();
        KnowledgeUserPageDTO model = params.getModel();

        if (StrUtil.isEmpty(model.getUserDomain())) {
            return R.fail("参数异常");
        }

        String sql = "SELECT knowledge_user_id FROM m_ai_knowledge_user_subscribe where user_domain = '"+ model.getUserDomain()+"' and subscribe_status = true";
        MembershipLevel membershipLevel = model.getMembershipLevel();
        if (Objects.nonNull(membershipLevel)) {
            sql += " and membership_level = '"+ membershipLevel.name()+"'";
        }
        // 根据用户的订阅关系表。查询订阅会员信息
        LbqWrapper<KnowledgeUser> wrapper = Wraps.<KnowledgeUser>lbQ()
                .apply("id in ( "+sql+" )");
        wrapper.orderByDesc(KnowledgeUser::getCreateTime);

        baseService.page(builtPage, wrapper);

        List<KnowledgeUser> records = builtPage.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> userIds = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
            List<KnowledgeUserSubscribe> userSubscribes = baseService.subscribeList(Wraps.<KnowledgeUserSubscribe>lbQ()
                    .in(KnowledgeUserSubscribe::getKnowledgeUserId, userIds)
                    .eq(KnowledgeUserSubscribe::getUserDomain, model.getUserDomain()));
            Map<Long, List<KnowledgeUserSubscribe>> collect = userSubscribes.stream().collect(Collectors.groupingBy(KnowledgeUserSubscribe::getKnowledgeUserId));
            for (KnowledgeUser record : records) {
                try {
                    if (StrUtil.isNotEmpty(record.getUserMobile())) {
                        record.setUserMobile(maskPhoneNumber(record.getUserMobile()));
                    }
                    List<KnowledgeUserSubscribe> subscribes = collect.get(record.getId());
                    KnowledgeUserSubscribe userSubscribe = subscribes.get(0);
                    record.setMembershipLevel(userSubscribe.getMembershipLevel());
                    record.setMembershipExpiration(userSubscribe.getMembershipExpiration());
                    record.setExperienceAiTime(userSubscribe.getExperienceAiTime());
                    record.setSubscribeTime(userSubscribe.getSubscribeTime());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }


        return R.success(builtPage);
    }

    @ApiOperation("查询当前用户详细信息")
    @GetMapping
    public R<KnowledgeUser> queryCurrentUser(@RequestParam String domain) {

        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = baseService.getById(userId);
        try {
            if (Objects.nonNull(knowledgeUser) && StrUtil.isNotEmpty(knowledgeUser.getUserMobile())) {
                knowledgeUser.setUserMobile(maskPhoneNumber(knowledgeUser.getUserMobile()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserDomain, domain)
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .last(" limit 1 "));
        Boolean subscribeSwitch = false;
        if (Objects.nonNull(user)) {
            subscribeSwitch = user.getSubscribeSwitch();
            if (Objects.isNull(subscribeSwitch)) {
                subscribeSwitch = false;
            }
        }
        KnowledgeUserSubscribe userSubscribe = baseService.getKnowledgeUserSubscribe(userId, domain, subscribeSwitch);
        if (Objects.nonNull(userSubscribe)) {
            knowledgeUser.setExperienceAiTime(userSubscribe.getExperienceAiTime());
            knowledgeUser.setMembershipLevel(userSubscribe.getMembershipLevel());
            knowledgeUser.setMembershipExpiration(userSubscribe.getMembershipExpiration());
            knowledgeUser.setSubscribeStatus(userSubscribe.getSubscribeStatus());
            knowledgeUser.setSubscribeTime(userSubscribe.getSubscribeTime());
        } else {
            knowledgeUser.setExperienceAiTime(null);
            knowledgeUser.setMembershipLevel(null);
            knowledgeUser.setMembershipExpiration(null);
            knowledgeUser.setSubscribeStatus(null);
            knowledgeUser.setSubscribeTime(null);
        }
        knowledgeUser.setShowSubscribeData(false);
        if (knowledgeUser.getUserName() == null) {
            knowledgeUser.setUserName("用户"+ knowledgeUser.getUserMobile().substring(knowledgeUser.getUserMobile().length() -4));
        }
        if (Objects.nonNull(user)) {
            // 博主订阅开关 关闭时，普通用户可以看到所有数据 ，博主可以看到所有数据
            if (!subscribeSwitch || user.getId().equals(knowledgeUser.getId())) {
                knowledgeUser.setShowSubscribeData(true);
                knowledgeUser.setOpenCaseDatabase(true);
                knowledgeUser.setOpenArticleData(true);
                knowledgeUser.setOpenTextual(true);
                knowledgeUser.setOpenAcademicMaterials(true);
                knowledgeUser.setOpenPersonalAchievements(true);
                if (user.getId().equals(knowledgeUser.getId())) {
                    knowledgeUser.setOpenDailyCollection(true);
                } else {
                    knowledgeUser.setOpenDailyCollection(false);
                }
            } else {
                // 博主订阅开关 开启时，会员用户可以看到数据
                if (knowledgeUser.getMembershipExpiration() != null && knowledgeUser.getMembershipExpiration().isAfter(LocalDateTime.now())) {
                    knowledgeUser.setShowSubscribeData(true);

                    // 只可见博主开放的数据
                    knowledgeUser.setOpenCaseDatabase(user.getOpenCaseDatabase());
                    knowledgeUser.setOpenArticleData(user.getOpenArticleData());
                    knowledgeUser.setOpenTextual(user.getOpenTextual());
                    knowledgeUser.setOpenAcademicMaterials(user.getOpenAcademicMaterials());
                    knowledgeUser.setOpenDailyCollection(user.getOpenDailyCollection());
                    knowledgeUser.setOpenPersonalAchievements(user.getOpenPersonalAchievements());
                } else {
                    knowledgeUser.setShowSubscribeData(false);
                    knowledgeUser.setOpenCaseDatabase(false);
                    knowledgeUser.setOpenArticleData(false);
                    knowledgeUser.setOpenTextual(false);
                    knowledgeUser.setOpenAcademicMaterials(false);
                    knowledgeUser.setOpenDailyCollection(false);
                    knowledgeUser.setOpenPersonalAchievements(false);
                }
            }
        }
        return R.success(knowledgeUser);
    }


    @ApiOperation("增加博主网站浏览次数")
    @GetMapping("anno/updateViewNumber")
    public R<Boolean> updateViewNumber(@RequestParam String domain) {

        UpdateWrapper<KnowledgeUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("number_views = number_views + 1");
        updateWrapper.eq("user_domain", domain);
        updateWrapper.eq("user_type", KnowDoctorType.CHIEF_PHYSICIAN.toString());
        baseService.update(updateWrapper);
        return R.success(true);
    }


    @ApiOperation("查询当前域名的博主信息")
    @GetMapping("anno/queryCurrentDomainUser")
    public R<KnowledgeUser> queryCurrentDomainUser(@RequestParam String domain) {

        KnowledgeUser knowledgeUser = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserDomain, domain)
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .last(" limit 1 "));
        try {
            if (Objects.nonNull(knowledgeUser) && StrUtil.isNotEmpty(knowledgeUser.getUserMobile())) {
                knowledgeUser.setUserMobile(maskPhoneNumber(knowledgeUser.getUserMobile()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Integer count = baseService.countFanNumber(domain);
        knowledgeUser.setFanNumber(count);
        return R.success(knowledgeUser);
    }




    /**
     * 查询子平台的博主信息
     * @param model
     * @param pageParams
     * @return
     */
    public IPage<KnowledgeUser> queryChildDomainUser(KnowledgeUserPageDTO model, PageParams<KnowledgeUserPageDTO> pageParams) {
        LbqWrapper<KnowledgeUser> lbqWrapper = Wraps.<KnowledgeUser>lbQ();

        // 子平台查询，需要依靠博主在子平台的域名设置和排序设置来排序
        KnowledgeMenuDomain menuDomain = knowledgeMenuDomainService.getOne(Wraps.<KnowledgeMenuDomain>lbQ()
                .eq(KnowledgeMenuDomain::getMenuDomain, model.getMenuDomain()));
        IPage<KnowledgeUser> builtPage = pageParams.buildPage();

        // 确定子平台是否存在
        if (Objects.isNull(menuDomain)) {
            return builtPage;
        }

        IPage<KnowledgeUserDomain> page = new Page(pageParams.getCurrent(), pageParams.getSize());
        LbqWrapper<KnowledgeUserDomain> userDomain = Wraps.<KnowledgeUserDomain>lbQ();

        userDomain.eq(KnowledgeUserDomain::getDomainName, model.getMenuDomain());
        if (StrUtil.isNotBlank(model.getKnowledgeMenuId())) {
            userDomain.eq(KnowledgeUserDomain::getDomainMenuId, model.getKnowledgeMenuId());
        }
        if (StrUtil.isNotBlank(model.getUserName())) {
            lbqWrapper.like(KnowledgeUser::getUserName, model.getUserName());
        }
        if (StrUtil.isNotBlank(model.getDepartment())) {
            lbqWrapper.like(KnowledgeUser::getDepartment, model.getDepartment());
        }
        if (StrUtil.isNotBlank(model.getDoctorTitle())) {
            lbqWrapper.like(KnowledgeUser::getDoctorTitle, model.getDoctorTitle());
        }
        lbqWrapper.select(SuperEntity::getId, KnowledgeUser::getUserName);
        lbqWrapper.eq(KnowledgeUser::getShowInDocuKnow, true);
        List<KnowledgeUser> knowledgeUsers = baseService.list(lbqWrapper);
        if (CollUtil.isEmpty(knowledgeUsers)) {
            return builtPage;
        }
        // 根据博主的姓名等调整。模糊查询博主
        List<Long> userIds = knowledgeUsers.stream().map(SuperEntity::getId).collect(Collectors.toList());
        userDomain.in(KnowledgeUserDomain::getKnowUserId, userIds);

        userDomain.orderByDesc(KnowledgeUserDomain::getSort);
        userDomain.orderByDesc(SuperEntity::getCreateTime);

        knowledgeUserDomainMapper.selectPage(page, userDomain);
        builtPage.setPages(page.getPages());
        builtPage.setTotal(page.getTotal());
        List<KnowledgeUserDomain> records = page.getRecords();
        // 提取出符合条件的博主的id
        userIds = records.stream().map(KnowledgeUserDomain::getKnowUserId).collect(Collectors.toList());
        if (CollUtil.isEmpty(userIds)) {
            return builtPage;
        }
        // 对博主列表进行排序
        knowledgeUsers = baseService.listByIds(userIds);
        Map<Long, KnowledgeUser> knowledgeUserMap = knowledgeUsers.stream().collect(Collectors.toMap(KnowledgeUser::getId, knowledgeUser -> knowledgeUser));
        List<KnowledgeUser> sortKnowledgeUsers = new ArrayList<>(knowledgeUsers.size());
        for (KnowledgeUserDomain record : records) {
            KnowledgeUser user = knowledgeUserMap.get(record.getKnowUserId());
            sortKnowledgeUsers.add(user);
        }
        builtPage.setRecords(sortKnowledgeUsers);
        return builtPage;

    }

    @ApiOperation("分页查询系统所有的博主信息")
    @PostMapping("anno/pageAllChiefPhysician")
    public R<IPage<KnowledgeUser>> pageAllChiefPhysician(
            HttpServletRequest request,
            @RequestBody PageParams<KnowledgeUserPageDTO> pageParams) {

        KnowledgeUserPageDTO model = pageParams.getModel();
        // 子平台用户过滤 使用 子平台平台域名 ， 分类分类
        if (StrUtil.isNotEmpty(model.getMenuDomain()) && !knowChildDomainConfig.getDocuknowMainDoamin().equals(model.getMenuDomain())) {
            // 查询子平台的博主信息
            IPage<KnowledgeUser> userIPage = queryChildDomainUser(model, pageParams);
            return R.success(userIPage);
        } else {
            LbqWrapper<KnowledgeUser> lbqWrapper = Wraps.<KnowledgeUser>lbQ();
            lbqWrapper.eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN);
            lbqWrapper.eq(KnowledgeUser::getShowInDocuKnow,  true);
            IPage<KnowledgeUser> builtPage = pageParams.buildPage();
            if (StrUtil.isNotBlank(model.getUserName())) {
                lbqWrapper.like(KnowledgeUser::getUserName, model.getUserName());
            }
            if (StrUtil.isNotBlank(model.getDepartment())) {
                lbqWrapper.like(KnowledgeUser::getDepartment, model.getDepartment());
            }
            if (StrUtil.isNotBlank(model.getDoctorTitle())) {
                lbqWrapper.like(KnowledgeUser::getDoctorTitle, model.getDoctorTitle());
            }
            lbqWrapper.orderByDesc(KnowledgeUser::getSort);
            lbqWrapper.orderByDesc(KnowledgeUser::getCreateTime);
            // 总平台 使用 总平台分类过滤。
            lbqWrapper.eq(KnowledgeUser::getMenuDomain, knowChildDomainConfig.getDocuknowMainDoamin());
            if (StrUtil.isNotBlank(model.getKnowledgeMenuId())) {
                lbqWrapper.eq(KnowledgeUser::getKnowledgeMenuId, model.getKnowledgeMenuId());
            }
            baseService.page(builtPage, lbqWrapper);
            for (KnowledgeUser user : builtPage.getRecords()) {
                if (Objects.nonNull(user) && StrUtil.isNotEmpty(user.getUserMobile())) {
                    user.setUserMobile(maskPhoneNumber(user.getUserMobile()));
                }
            }
            return R.success(builtPage);
        }


    }




    @ApiOperation("查询系统所有的博主信息")
    @GetMapping("anno/queryAllChiefPhysician")
    public R<List<KnowledgeUser>> queryAllChiefPhysician() {

        List<KnowledgeUser> knowledgeUsers = baseService.list(Wraps.<KnowledgeUser>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN));
        for (KnowledgeUser user : knowledgeUsers) {
            if (Objects.nonNull(user) && StrUtil.isNotEmpty(user.getUserMobile())) {
                user.setUserMobile(maskPhoneNumber(user.getUserMobile()));
            }
        }
        return R.success(knowledgeUsers);
    }



    @ApiOperation("修改密码")
    @PutMapping("updatePassword")
    public R<Boolean> updatePassword(@RequestBody @Validated UserUpdatePasswordDTO updatePassword) {

        Long id = updatePassword.getId();
        String password = updatePassword.getPassword();

        Long userId = BaseContextHandler.getUserId();
        if (!userId.equals(id)) {
            throw new BizException(I18nUtils.getMessage("user_not_exist"));
        }
        if (!PasswordUtils.validatePassword(password)) {
            return R.fail("密码请设置8位以上，包含数字、字母或符号");
        }

        KnowledgeUser user = new KnowledgeUser();
        user.setId(id);
        user.setPassword(SecureUtil.md5(password));
        baseService.updateById(user);
        return R.success(true);

    }


    @ApiOperation("重置密码")
    @PostMapping("anno/resetPassword")
    public R<Boolean> resetPassword(@RequestBody @Validated UserResetPasswordDTO userResetPassword) {

        String mobile = userResetPassword.getUserMobile();
        String smsCode = userResetPassword.getSmsCode();
        String password = userResetPassword.getPassword();
        mobile = mobile.trim();
        KnowledgeUser user = null;
        if (!PasswordUtils.validatePassword(password)) {
            return R.fail("密码请设置8位以上，包含数字、字母或符号");
        }
        try {
            user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(mobile))
                    .orderByAsc(SuperEntity::getCreateTime)
                    .last(" limit 0,1 "));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            throw new BizException(I18nUtils.getMessage("user_not_exist"));
        }
        checkSMS(mobile, smsCode, true);
        user.setUserMobile(mobile);
        user.setPassword(SecureUtil.md5(password));
        baseService.updateById(user);
        return R.success(true);

    }


    @ApiOperation("检查验证码是否正确")
    @PostMapping("anno/checkSmsCode")
    public R<Boolean> checkSmsCode(@RequestBody @Validated UserResetPasswordCodeDTO userResetPassword) {
        String mobile = userResetPassword.getUserMobile();
        String smsCode = userResetPassword.getSmsCode();

        checkSMS(mobile, smsCode, false);

        return R.success(true);

    }

    @ApiOperation("检查账号是否存在")
    @GetMapping("anno/checkMobileExist")
    public R<Boolean> checkMobileExist(@RequestParam String userAccount) {
        try {
            KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(userAccount))
                    .last(" limit 0,1 "));
            return R.success(user == null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @ApiOperation("使用账号密码注册")
    @PostMapping("anno/register")
    public R<JSONObject> register(@RequestBody @Validated ArticleUserSaveDTO articleUserSaveDTO) throws Exception {

        String userMobile = articleUserSaveDTO.getUserMobile();
        String password = articleUserSaveDTO.getPassword();
        userMobile = userMobile.trim();
        // 密码校验
        if (StrUtil.isNotBlank(password)) {
            if (!PasswordUtils.validatePassword(password)) {
                return R.fail("密码请设置8位以上，包含数字、字母或符号");
            }
            KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(userMobile))
                    .last(" limit 0,1 "));
            if (Objects.nonNull(user)) {
                return R.fail("账号已存在");
            }
            user = new KnowledgeUser();
            user.setUserMobile(userMobile);
            user.setPassword(SecureUtil.md5(password));
            user.setUserType(KnowDoctorType.GENERAL_PRACTITIONER);
            user.setUserName("用户"+ userMobile.substring(userMobile.length() -4));
            user.setMembershipLevel(MembershipLevel.FREE_VERSION);
            baseService.save(user);

            // 验证通过,授权token
            R<JSONObject> token = null;
            token = oauthApi.createTempToken(user.getId());
            // 添加其它参数

            token.getData().put("userType", user.getUserType());
            token.getData().put("userAvatar", user.getUserAvatar());
            token.getData().put("userName", user.getUserName());
            token.getData().put("userMobile", maskPhoneNumber(userMobile));
            return token;
        } else {
            throw new BizException(I18nUtils.getMessage("password_error"));
        }

    }

    @ApiOperation("使用手机号登录或注册")
    @PostMapping("anno/aiMobileLogin")
    public R<JSONObject> aiMobileLogin(@RequestBody @Validated KnowledgeUserSaveDTO knowledgeUserSaveDTO) {


        String userMobile = knowledgeUserSaveDTO.getUserMobile();

        userMobile = userMobile.trim();
        KnowledgeUser user = checkUserPasswordOrCode(knowledgeUserSaveDTO);

        // 是否是博主首次使用验证码登录
        Boolean doctorFirstLogin = false;
        R<JSONObject> token = null;
        if (Objects.isNull(user)) {
            user = new KnowledgeUser();
            user.setUserMobile(userMobile);
            user.setUserType(KnowDoctorType.GENERAL_PRACTITIONER);
            user.setUserName("用户"+ userMobile.substring(userMobile.length() -4));
            user.setMembershipLevel(MembershipLevel.FREE_VERSION);
            baseService.save(user);
        }
        // 是否首次使用验证码登录
        if (user.getSmsCodeLoginFirstTime() == null) {
            user.setSmsCodeLoginFirstTime(LocalDateTime.now());

            // 是否是博主
            if (user.getUserType() == KnowDoctorType.CHIEF_PHYSICIAN) {
                doctorFirstLogin = true;
            }
        }
        user.setSmsCodeLoginLastTime(LocalDateTime.now());
        baseService.updateById(user);
        token = oauthApi.createTempToken(user.getId());

        token.getData().put("userType", user.getUserType());
        token.getData().put("userAvatar", user.getUserAvatar());
        token.getData().put("userName", user.getUserName());
        token.getData().put("userMobile", maskPhoneNumber(userMobile));
        token.getData().put("doctorFirstLogin", doctorFirstLogin);
        return token;

    }

    public static String maskPhoneNumber(String phoneNumber) {
        // 检查手机号是否为11位数字
        if (phoneNumber == null) {
            return null;
        }
        if (phoneNumber.length() != 11 || !phoneNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid phone number.");
        }

        // 使用 substring 和 replaceFirst 方法来替换中间4位数字
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }



    @ApiOperation("发送一条登录的短信")
    @PostMapping("anno/sendSms")
    public boolean sendSms(@RequestBody SmsDto smsDto) {

        String phone = smsDto.getPhone();
        // 限制一小时只能发10条短信
        String key = "ai_know_sms_count" + phone;
        String string = redisTemplate.opsForValue().get(key);
        if (string != null && Integer.parseInt(string) > 10) {
            throw new BizException(I18nUtils.getMessage("sms_count_error"));
        }
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey == null || !hasKey) {
            redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        } else {
            redisTemplate.opsForValue().increment(key, 1);
        }

        String smsCode = RandomUtil.randomNumbers(6);
        System.out.println("短信验证码： " + smsCode);
        String sendSMS = smsSendService.sendSMS(phone, smsCode);
        if (StrUtil.isEmpty(sendSMS)) {
            throw new BizException("短信验证码发送失败!");
        }
        BoundSetOperations<String, String> operations = redisTemplate.boundSetOps("ai_know_sms_" + phone);
        operations.add(smsCode);
        redisTemplate.boundSetOps(key).expire(1, TimeUnit.HOURS);

        return true;
    }

    /**
     * 校验短信验证码是否存在
     * @param phone
     * @param code
     */
    public void checkSMS(String phone, String code, Boolean clearKey) {
        if (!StringUtils.isEmpty(phone) && !StringUtils.isEmpty(code)) {

            // 验证 验证码是否是创建博主账号是，生成的验证码
            Boolean aBoolean = redisTemplate.boundSetOps("ai_know_temp_code:" + phone).isMember(code);
            if (aBoolean != null && aBoolean) {
                return;
            }

            String key = "ai_know_sms_" + phone;
            Boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey == null || !hasKey) {
                throw new BizException(I18nUtils.getMessage("SMS_TIME_OUT"));
            }
            BoundSetOperations<String, String> operations = redisTemplate.boundSetOps(key);
            Boolean member = operations.isMember(code);
            if (member == null || !member) {
                throw new BizException(I18nUtils.getMessage("SMS_TIME_OUT"));
            }
            if (clearKey) {
                redisTemplate.delete(key);
            }
        } else {
            throw new BizException("手机号或手机验证码不能为空!");
        }
    }




    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @ApiOperation("获取用户信息")
    @GetMapping("anno/getUser")
    public R<KnowledgeUser> getUser(@RequestParam Long userId) {
        KnowledgeUser knowledgeUser = baseService.getById(userId);
        if (Objects.isNull(knowledgeUser)) {
            throw new BizException("用户不存在!");
        }
        knowledgeUser.setUserMobile(maskPhoneNumber(knowledgeUser.getUserMobile()));
        return R.success(knowledgeUser);
    }

    /**
     * 获取AI工作室医生的博主信息
     * @param userId
     * @return
     */
    @ApiOperation("获取AI工作室医生的博主信息")
    @GetMapping("anno/getStudioDoctorId")
    public R<KnowledgeUser> getStudioDoctorId(@RequestParam Long userId) {
        KnowledgeUser knowledgeUser = baseService.getOne(Wrappers.<KnowledgeUser>lambdaQuery()
                .last("limit 1")
                .eq(KnowledgeUser::getAiStudioDoctorId, userId));
        if (Objects.isNull(knowledgeUser)) {
            throw new BizException("用户不存在!");
        }
        knowledgeUser.setUserMobile(maskPhoneNumber(knowledgeUser.getUserMobile()));
        return R.success(knowledgeUser);
    }



    @ApiOperation("创建一个主任医生账号，并生成知识库")
    @PostMapping("createKnowledgeUser")
    public R<KnowledgeUser> createKnowledgeUser(@RequestBody @Validated KnowledgeUser knowledgeUser, @RequestParam(required = false) Boolean copyFile) {
        String userMobile = knowledgeUser.getUserMobile();
        copyFile = copyFile == null || copyFile;
        baseService.createKnowledgeUser(knowledgeUser, copyFile, true);
        if (StrUtil.isNotBlank(userMobile) && userMobile.length() > 6) {
            String code = userMobile.substring(userMobile.length() - 6);
            redisTemplate.boundSetOps("ai_know_temp_code:" + userMobile)
                    .add(code);
        }
        return R.success(knowledgeUser);
    }



    @ApiOperation("处理博主的数字人图片")
    @GetMapping("importKnowledgeUser")
    public R<Void> importKnowledgeUser() {

        LocalDateTime now = LocalDateTime.now();

        File file = new File("/mnt/data/caring-ai/数字人头像");
        File[] listFiles = file.listFiles();
        for (File listFile : listFiles) {
            try {
                String name = listFile.getName();
                String[] split = name.split("\\.");
                String doctorName =  split[0];
                System.out.println(doctorName);
                KnowledgeUser knowledgeUser = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                        .select(SuperEntity::getId, KnowledgeUser::getUserName, KnowledgeUser::getUserAvatar)
                        .eq(KnowledgeUser::getUserName, doctorName)
                        .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                        .last(" limit 0, 1 "));
                if (knowledgeUser != null) {
                    if (file.exists()) {
                        R<com.caring.sass.file.entity.File> uploaded = fileUploadApi.upload(1L, FileUtils.fileToFileItem(listFile));
                        com.caring.sass.file.entity.File data = uploaded.getData();
                        if (data != null) {
                            knowledgeUser.setUserAvatar(data.getUrl());
                            knowledgeUser.setUpdateUser(1L);
                            knowledgeUser.setUpdateTime(now);
                            baseService.updateById(knowledgeUser);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return R.success(null);

    }

    @ApiOperation("切换用户的分身通话开关")
    @PostMapping("switchCallStatus/{userId}")
    public R<Boolean> switchCallStatus(@PathVariable Long userId) {

        KnowledgeUser knowledgeUser = baseService.getById(userId);
        if (Objects.isNull(knowledgeUser)) {
            throw new BizException("用户不存在!");
        }
        if (knowledgeUser.getCallSwitch() == null || knowledgeUser.getCallSwitch() == 0) {
            knowledgeUser.setCallSwitch(1);
        } else {
            knowledgeUser.setCallSwitch(0);
        }
        baseService.updateById(knowledgeUser);
        return R.success(true);
    }


    @ApiOperation("获取博主在saas的二维码")
    @GetMapping("getQrCode")
    public R<SaasDoctorQrCode> getQrCode(@RequestParam String domain) {

        KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserDomain, domain)
                .last(" limit 0,1 "));
        String userMobile = user.getUserMobile();
        if (StrUtil.isEmpty(user.getSaasTenantCode())) {
            return R.success(null);
        }
        R<Tenant> tenantR = tenantApi.getByCode(user.getSaasTenantCode());
        Tenant data = tenantR.getData();
        String doctorQrUrl = data.getDoctorQrUrl();
        R<String> patientQrCode = doctorApi.getPatientQrCode(userMobile, user.getSaasTenantCode());
        String qrCodeData = patientQrCode.getData();
        SaasDoctorQrCode doctorQrCode = new SaasDoctorQrCode();
        doctorQrCode.setDoctorQrUrl(doctorQrUrl);
        doctorQrCode.setQrCodeData(qrCodeData);
        return R.success(doctorQrCode);

    }



    /**
     * 统计博主数据
     */
    @ApiOperation("统计博主所有数据")
    @GetMapping("countUserAllData")
    public R<KnowsUserDataCountModel> countUserAllData(@RequestParam String domain) {
        KnowsUserDataCountModel countModel = aiUserService.countUserAllData(domain);
        return R.success(countModel);
    }

    @ApiOperation("更新博主在saas的医生激活码")
    @GetMapping("anno/updateKnowuserSaasTenantCode")
    public R<String> updateKnowuserSaasTenantCode(String userMobile, String tenantCode) {
        try {
            KnowledgeUser user = baseService.getOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(userMobile))
                    .last(" limit 0,1 "));
            if (Objects.isNull(user)) {
                return R.fail("手机号未查询到博主");
            }
            user.setSaasTenantCode(tenantCode);
            boolean codeCheck = baseService.updateById(user);
            return R.success(codeCheck ? "更新成功" : "更新失败");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



}
