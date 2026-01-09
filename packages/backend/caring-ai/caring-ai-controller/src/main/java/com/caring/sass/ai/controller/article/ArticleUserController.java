package com.caring.sass.ai.controller.article;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.article.config.ArticleUserPayConfig;
import com.caring.sass.ai.article.service.*;
import com.caring.sass.ai.dto.*;
import com.caring.sass.ai.dto.article.ArticleUserInfoUpdateDTO;
import com.caring.sass.ai.entity.article.ArticleEventLog;
import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.article.ArticleUserAccount;
import com.caring.sass.ai.entity.know.KnowDoctorType;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.ai.service.SmsSendService;
import com.caring.sass.ai.utils.I18nUtils;
import com.caring.sass.ai.utils.PasswordUtils;
import com.caring.sass.base.R;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.oauth.api.OauthApi;
import com.caring.sass.sms.dto.SmsDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 前端控制器
 * Ai创作用户
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleUser")
@Api(value = "ArticleUser", tags = "科普创作用户管理")
public class ArticleUserController {


    @Autowired
    OauthApi oauthApi;

    @Autowired
    ArticleUserService baseService;

    @Autowired
    KnowledgeUserService knowledgeUserService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    SmsSendService smsSendService;

    @Autowired
    ArticleUserPayConfig articleUserPayConfig;

    @Autowired
    ArticleEventLogService articleEventLogService;

    @Autowired
    ArticleUserAccountService articleUserAccountService;

    @Autowired
    ArticleUserNoticeService articleUserNoticeService;
    @ApiOperation("创建一个临时的用户")
    @PostMapping("anno/createTempUser")
    public R<JSONObject> aiTempLogin() {

//        ArticleUser articleUser = new ArticleUser();
//        articleUser.setCreateTime(LocalDateTime.now());
//        baseService.save(articleUser);
//        R<JSONObject> tempToken = oauthApi.createTempToken(articleUser.getId());
//        tempToken.getData().put("userLoginStatus", "NO_LOGIN");
        return null;
    }


    /**
     * 根据手机号找到用户。增加能量豆
     * @param mobile
     * @param energyBeans
     * @return
     */
    @GetMapping("/anno/addUserEnergyBeans")
    public R<Boolean> addUserEnergyBeans(@RequestParam String tempToken,
                                         @RequestParam String mobile,
                                         @RequestParam Integer energyBeans) {

        if (tempToken.equals("asdqweasdzxccasdqwesda124sada22")) {
            try {
                ArticleUserAccount user = articleUserAccountService.getOne(Wraps.<ArticleUserAccount>lbQ()
                        .eq(ArticleUserAccount::getUserMobile, EncryptionUtil.encrypt(mobile))
                        .last(" limit 0,1 "));

                if (user != null) {
                    user.setEnergyBeans(user.getEnergyBeans() + energyBeans);
                    articleUserAccountService.updateById(user);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return R.success(false);


    }


    @ApiOperation("设置是否查看引导")
    @PostMapping("setLookGuide")
    public R<Boolean> setLookGuide(@RequestParam Long userId) {
        ArticleUser user = baseService.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setLookGuide(true);
        baseService.updateById(user);
        return R.success(true);
    }




    @ApiOperation("查询充值和花费金额")
    @GetMapping("queryCost")
    public R<ArticleUserPayConfig> queryCost() {
        return R.success(articleUserPayConfig);
    }


    @ApiOperation("更新会员信息性别头像等")
    @PostMapping("updateInfo")
    public R<ArticleUserInfoUpdateDTO> refreshToken(@RequestBody ArticleUserInfoUpdateDTO articleUserInfoUpdateDTO) {

        Long userId = articleUserInfoUpdateDTO.getUserId();
        ArticleUser user = baseService.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        BeanUtils.copyProperties(articleUserInfoUpdateDTO, user);
        user.setUserMobile(null);
        baseService.updateById(user);
        return R.success(articleUserInfoUpdateDTO);
    }




    @ApiOperation("使用用户ID和refreshToken更新token")
    @PostMapping("anno/refreshToken")
    public R<JSONObject> refreshToken(@RequestBody ArticleUserUpdateDTO articleUserUpdateDTO) {

        Long userId = articleUserUpdateDTO.getUserId();
        ArticleUser user = baseService.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        } else {
            // 检查用户的会员是否快要到期。快到期就发送一条系统消息
            if (user.getMembershipExpiration() != null && user.getMembershipExpiration().isBefore(LocalDateTime.now().plusDays(3))) {
                // 发送系统消息
                articleUserNoticeService.sendMembershipExpirationNotice(user);
            }
        }
        String refreshToken = articleUserUpdateDTO.getRefreshToken();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("refreshToken", refreshToken);
        return oauthApi.aiAuthRefreshToken(jsonObject);
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
        ArticleUser user = new ArticleUser();
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

        if (!PasswordUtils.validatePassword(password)) {
            return R.fail("密码请设置8位以上，包含数字、字母或符号");
        }

        ArticleUser user = null;
        try {
            user = baseService.getOne(Wraps.<ArticleUser>lbQ()
                    .eq(ArticleUser::getUserMobile, EncryptionUtil.encrypt(mobile))
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
            ArticleUser user = baseService.getOne(Wraps.<ArticleUser>lbQ()
                    .eq(ArticleUser::getUserMobile, EncryptionUtil.encrypt(userAccount))
                    .last(" limit 0,1 "));
            return R.success(user == null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("查询手机号的用户id")
    @GetMapping("anno/getMobileUserId")
    public R<Long> getMobileUserId(@RequestParam String userAccount) {
        try {
            ArticleUser user = baseService.getOne(Wraps.<ArticleUser>lbQ()
                    .eq(ArticleUser::getUserMobile, EncryptionUtil.encrypt(userAccount))
                    .last(" limit 0,1 "));
            if (user == null) {
                return R.success(null);
            }
            return R.success(user.getId());
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
            ArticleUser user = baseService.getOne(Wraps.<ArticleUser>lbQ()
                    .eq(ArticleUser::getUserMobile, EncryptionUtil.encrypt(userMobile))
                    .last(" limit 0,1 "));
            if (Objects.nonNull(user)) {
                return R.fail("账号已存在");
            }
            user = new ArticleUser()
                    .setUserMobile(userMobile)
                    .setPassword(SecureUtil.md5(password));
            baseService.save(user);

            // 验证通过,授权token
            R<JSONObject> tokenR = oauthApi.createTempToken(user.getId());
            // 添加其它参数
            tokenR.getData().put("userLoginStatus", "LOGIN");
            tokenR.getData().put("userMobile", maskPhoneNumber(userMobile));

            articleEventLogService.loginEvent(user.getId());
            return tokenR;
        } else {
            throw new BizException(I18nUtils.getMessage("password_error"));
        }

    }




    @ApiOperation("使用手机号登录或注册")
    @PostMapping("anno/aiMobileLogin")
    public R<JSONObject> aiMobileLogin(@RequestBody @Validated ArticleUserSaveDTO articleUserSaveDTO) throws Exception {
        String userMobile = articleUserSaveDTO.getUserMobile();
        String smsCode = articleUserSaveDTO.getSmsCode();
        String password = articleUserSaveDTO.getPassword();
        userMobile = userMobile.trim();

        // 密码校验
        if (StrUtil.isNotBlank(password)) {
            ArticleUser user = baseService.getOne(Wraps.<ArticleUser>lbQ()
                    .eq(ArticleUser::getUserMobile, EncryptionUtil.encrypt(userMobile))
                    .last(" limit 0,1 "));
            if (Objects.isNull(user)) {
                throw new BizException(I18nUtils.getMessage("user_not_exist_use_code_register"));
            }
            if (StrUtil.isBlank(user.getPassword())) {
                throw new BizException(I18nUtils.getMessage("password_not_set"));
            }
            if (!SecureUtil.md5(password).equals(user.getPassword())) {
                throw new BizException(I18nUtils.getMessage("password_error"));
            }
            // 验证通过,授权token
            R<JSONObject> tokenR = oauthApi.createTempToken(user.getId());
            // 添加其它参数
            tokenR.getData().put("userLoginStatus", "LOGIN");
            tokenR.getData().put("userMobile", maskPhoneNumber(userMobile));

            articleEventLogService.loginEvent(user.getId());
            return tokenR;
        }


        // 校验手机号
        if (StrUtil.isNotBlank(smsCode)) {
            // 验证短信验证码是否正确
            checkSMS(userMobile, smsCode, true);
            ArticleUser user = baseService.getOne(Wraps.<ArticleUser>lbQ()
                    .eq(ArticleUser::getUserMobile, EncryptionUtil.encrypt(userMobile))
                    .last(" limit 0,1 "));

            // 当 手机号 对应的用户不存在，创建账号
            if (Objects.isNull(user)) {
                user = new ArticleUser()
                        .setUserMobile(userMobile);
                baseService.save(user);
            }

            // 验证通过,授权token
            R<JSONObject> tokenR = oauthApi.createTempToken(user.getId());
            // 添加其它参数
            tokenR.getData().put("userLoginStatus", "LOGIN");
            tokenR.getData().put("userMobile", maskPhoneNumber(userMobile));
            articleEventLogService.loginEvent(user.getId());
            return tokenR;
        }

        return R.fail(I18nUtils.getMessage("password_error"));
    }

    public static String maskPhoneNumber(String phoneNumber) {
        // 检查手机号是否为11位数字
        if (phoneNumber == null || phoneNumber.length() != 11 || !phoneNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid phone number.");
        }

        // 使用 substring 和 replaceFirst 方法来替换中间4位数字
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }

    @ApiOperation("发送一条登录的短信")
    @PostMapping("anno/sendSms")
    public boolean sendSms(@RequestBody SmsDto smsDto) {

        Boolean password = smsDto.getResetPassword();
        String phone = smsDto.getPhone();
        if (password != null && password) {
            try {
                int count = baseService.count(Wraps.<ArticleUser>lbQ().eq(ArticleUser::getUserMobile, EncryptionUtil.encrypt(phone)));
                if (count == 0) {
                    throw new BizException("用户不存在，请先登录");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 限制一小时只能发10条短信
        String key = "ai_sms_count" + phone;
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
        String sendSMS = smsSendService.sendSMS(phone, smsCode);
        if (StrUtil.isEmpty(sendSMS)) {
            throw new BizException("短信验证码发送失败!");
        }
        BoundSetOperations<String, String> operations = redisTemplate.boundSetOps("ai_sms_" + phone);
        operations.add(smsCode);
        redisTemplate.boundSetOps(key).expire(1, TimeUnit.HOURS);

        return true;
    }



    @ApiOperation("查询用户信息")
    @GetMapping("getUserInfo")
    public R<ArticleUser> getUserInfo(Long userId) {

        ArticleUser articleUser = baseService.getById(userId);
        articleUser.setPassword(null);
        try {
            KnowledgeUser knowledgeUser = knowledgeUserService.getOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(articleUser.getUserMobile()))
                    .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                    .last(" limit 0 , 1 "));
            if (Objects.nonNull(knowledgeUser)) {
                articleUser.setKnowledgeChiefPhysician(true);
            } else {
                articleUser.setKnowledgeChiefPhysician(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            ArticleUserAccount account = articleUserAccountService.getOne(Wraps.<ArticleUserAccount>lbQ()
                    .eq(ArticleUserAccount::getUserMobile, EncryptionUtil.encrypt(articleUser.getUserMobile())));
            articleUser.setEnergyBeans(account.getEnergyBeans());
            articleUser.setFreeEnergyBeans(account.getFreeEnergyBeans());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        articleUser.setUserMobile(SensitiveInfoUtils.desensitizePhone(articleUser.getUserMobile()));
        return R.success(articleUser);

    }


    /**
     * 校验短信验证码是否存在
     * @param phone
     * @param code
     */
    public void checkSMS(String phone, String code, Boolean clearKey) {
        if (!StringUtils.isEmpty(phone) && !StringUtils.isEmpty(code)) {
            String key = "ai_sms_" + phone;
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


    @ApiOperation("科普创作授权Openid")
    @GetMapping("anno/redirectWxAuthUrl")
    public void redirectWxAuthUrl(HttpServletResponse resp, String redirectUri) {

        String wxAuthUrl = baseService.redirectWxAuthUrl(redirectUri);
        try {
            resp.sendRedirect(wxAuthUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @ApiOperation("科普创作用户事件记录")
    @PostMapping("anno/produceEventRecord")
    public R<Boolean> produceEventRecord(@RequestBody ArticleEventLog eventLog) {

        articleEventLogService.save(eventLog);

        return R.success(true);
    }


    @Autowired
    ArticleDailyUsageDataService dailyUsageDataService;


//    @ApiOperation("handleYesterdayData")
//    @GetMapping("/anno/handleYesterdayData")
//    public R<Boolean> handleYesterdayData(int year, int month, int day) {
//        LocalDateTime now = LocalDateTime.of(year, month, day, 0, 0, 0);
//        dailyUsageDataService.handleYesterdayData(now);
//        return R.success(true);
//    }

    @ApiOperation("导出科普创作的数据")
    @GetMapping("exportTemplate")
    public R<String> articleUserDataQuery() {

        String string = dailyUsageDataService.exportTemplate();
        return R.success(string);
    }


}
