package com.caring.sass.ai.controller.card;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.card.dao.BusinessCardStudioMapper;
import com.caring.sass.ai.card.service.BusinessCardAdminService;
import com.caring.sass.ai.card.service.BusinessCardService;
import com.caring.sass.ai.dto.*;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.card.BusinessCardAdmin;
import com.caring.sass.ai.entity.card.BusinessCardStudio;
import com.caring.sass.ai.service.SmsSendService;
import com.caring.sass.ai.utils.I18nUtils;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.oauth.api.OauthApi;
import com.caring.sass.sms.dto.SmsDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 前端控制器
 * 科普名片管理员
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessCardAdmin")
@Api(value = "BusinessCardAdmin", tags = "科普名片管理员")
public class BusinessCardAdminController  {

    @Autowired
    BusinessCardAdminService businessCardAdminService;

    @Autowired
    BusinessCardStudioMapper businessCardStudioMapper;

    @Autowired
    BusinessCardService businessCardService;

    @Autowired
    OauthApi oauthApi;


    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    SmsSendService smsSendService;
    /**
     * 更新名片所属的机构
     * @param businessCardOrganDTO
     * @return
     */
    @ApiOperation("更新名片所属的机构")
    @PutMapping("updateCardOrgan")
    public R<Boolean> updateCardOrgan(@RequestBody BusinessCardOrganDTO businessCardOrganDTO) {

        businessCardAdminService.updateCardOrgan(businessCardOrganDTO.getId(), businessCardOrganDTO.getOrganId());
        return R.success(true);

    }

    @ApiOperation("清除微信小程序用户")
    @DeleteMapping("clearUserId")
    public R<Boolean> clearUserId(@RequestParam("id") Long id) {

        businessCardAdminService.clearUserId(id);
        return R.success(true);

    }


    @ApiOperation("新增管理员")
    @PostMapping("save")
    public R<BusinessCardAdmin> save(@RequestBody BusinessCardAdmin businessCardAdmin) {
        String password = businessCardAdmin.getPassword();
        if (StrUtil.isNotBlank(password)) {
            businessCardAdmin.setPassword(SecureUtil.md5(password));
        }
        businessCardAdminService.save(businessCardAdmin);
        return R.success(businessCardAdmin);

    }


    @ApiOperation("查询用户信息")
    @GetMapping()
    public R<BusinessCardAdmin> get(@RequestParam("userId") Long userId) {

        BusinessCardAdmin businessCardAdmin = businessCardAdminService.getById(userId);
        businessCardAdmin.setUserMobile(maskPhoneNumber(businessCardAdmin.getUserMobile()));
        return R.success(businessCardAdmin);

    }

    @ApiOperation("删除名片")
    @SysLog("删除名片")
    @DeleteMapping("deleteCard")
    @Transactional
    public R<BusinessCard> deleteCard(@RequestParam("cardId") Long cardId) {
        BusinessCard businessCard = businessCardService.getById(cardId);
        if (Objects.isNull(businessCard)) {
            return R.fail("名片不存在");
        }
        businessCardService.removeById(cardId);
        return R.success(businessCard);
    }


    @ApiOperation("修改名片内容")
    @SysLog("修改医生AI名片")
    @PutMapping("updateBusinessCard")
    @Transactional
    public R<BusinessCard> updateBusinessCard(@RequestBody @Validated BusinessCardUpdateDTO businessCardUpdateDTO) {
        Long id = businessCardUpdateDTO.getId();
        BusinessCard businessCard = businessCardService.getById(id);
        if (Objects.isNull(businessCard)) {
            return R.fail("名片不存在");
        }
        boolean resetBusinessCard = false;
        if (StrUtil.isBlank(businessCardUpdateDTO.getDoctorHospital()) ||
            StrUtil.isBlank(businessCardUpdateDTO.getDoctorName()) ||
            StrUtil.isBlank(businessCardUpdateDTO.getDoctorTitle()) ||
            StrUtil.isBlank(businessCardUpdateDTO.getDoctorAvatar()) ||
            StrUtil.isBlank(businessCardUpdateDTO.getDoctorDepartment())) {
            return R.fail("医生姓名，职称，医院，科室，头像不能为空");
        }

        if (businessCard.getDoctorHospital().equals(businessCardUpdateDTO.getDoctorHospital())) {
            resetBusinessCard = true;
        } else if (businessCard.getDoctorDepartment().equals(businessCardUpdateDTO.getDoctorDepartment())) {
            resetBusinessCard = true;
        } else if (businessCard.getDoctorName().equals(businessCardUpdateDTO.getDoctorName())) {
            resetBusinessCard = true;
        } else if (businessCard.getDoctorTitle().equals(businessCardUpdateDTO.getDoctorTitle())) {
            resetBusinessCard = true;
        } else if (businessCard.getDoctorAvatar().equals(businessCardUpdateDTO.getDoctorAvatar())) {
            resetBusinessCard = true;
        }

        List<BusinessCardStudio> cardStudios = businessCardUpdateDTO.getStudios();
        BeanUtils.copyProperties(businessCardUpdateDTO, businessCard);
        businessCardService.checkVideoInfo(businessCard);
        businessCard.setResetBusinessQrCode(resetBusinessCard);
        businessCardService.updateAllById(businessCard);
        // 删除工作室
        businessCardStudioMapper.delete(Wraps.<BusinessCardStudio>lbQ().eq(BusinessCardStudio::getBusinessCard, id));
        if (CollUtil.isNotEmpty(cardStudios)) {
            cardStudios.forEach(item -> item.setBusinessCard(id));
            businessCardStudioMapper.insertBatchSomeColumn(cardStudios);
        }
        return R.success(businessCard);
    }



    @ApiOperation("创建AI医生名片")
    @SysLog("创建AI医生名片")
    @PutMapping("createBusinessCard")
    @Transactional
    public R<BusinessCard> createBusinessCard(@RequestBody @Validated BusinessCardSaveDTO businessCardSaveDTO) {

        BusinessCard card = new BusinessCard();
        if (StrUtil.isBlank(businessCardSaveDTO.getDoctorHospital()) ||
                StrUtil.isBlank(businessCardSaveDTO.getDoctorName()) ||
                StrUtil.isBlank(businessCardSaveDTO.getDoctorTitle()) ||
                StrUtil.isBlank(businessCardSaveDTO.getDoctorAvatar()) ||
                StrUtil.isBlank(businessCardSaveDTO.getDoctorDepartment())) {
            return R.fail("医生姓名，职称，医院，科室，头像不能为空");
        }

        List<BusinessCardStudio> cardStudios = businessCardSaveDTO.getStudios();
        BeanUtils.copyProperties(businessCardSaveDTO, card);
        businessCardService.checkVideoInfo(card);
        card.setHasDoctorStudio(CommonStatus.NO);
        card.setCreateButtonStatus(CommonStatus.NO);
        card.setOrganId(businessCardSaveDTO.getOrganId());
        businessCardService.save(card);
        Long cardId = card.getId();
        if (CollUtil.isNotEmpty(cardStudios)) {
            cardStudios.forEach(item -> item.setBusinessCard(cardId));
            businessCardStudioMapper.insertBatchSomeColumn(cardStudios);
        }
        return R.success(card);

    }

    /**
     * 设置医生的多个工作室
     * @param businessCard
     */
    private void setDoctorStudio(BusinessCard businessCard) {
        List<BusinessCardStudio> studios = businessCardStudioMapper.selectList(Wraps.<BusinessCardStudio>lbQ()
                .eq(BusinessCardStudio::getBusinessCard, businessCard.getId()));
        businessCard.setStudios(studios);
    }


    @ApiOperation("获取名片详情")
    @GetMapping("getCard")
    public R<BusinessCard> getCard(@RequestParam Long cardId) {


        BusinessCard businessCard = businessCardService.getById(cardId);
        if (Objects.isNull(businessCard)) {
            return R.fail("名片不存在");
        }
        setDoctorStudio(businessCard);
        return R.success(businessCard);

    }

    @ApiOperation("手机号用户是否存在")
    @GetMapping("anno/existUserMobile")
    public R<Boolean> countUserMobile(@RequestParam String mobile) {

        LbqWrapper<BusinessCardAdmin> lbqWrapper = null;
        try {
            lbqWrapper = Wraps.<BusinessCardAdmin>lbQ().eq(BusinessCardAdmin::getUserMobile, EncryptionUtil.encrypt(mobile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int count = businessCardAdminService.count(lbqWrapper);
        if (count > 0) {
            return R.success(true);
        } else {
            return R.success(false);
        }
    }

    @ApiOperation("使用用户ID和refreshToken更新token")
    @PostMapping("anno/refreshToken")
    public R<JSONObject> refreshToken(@RequestBody BusinessCardTokenReset userUpdateDTO) {

        Long userId = userUpdateDTO.getUserId();
        BusinessCardAdmin user = businessCardAdminService.getById(userId);
        if (user == null) {
            throw new BizException(I18nUtils.getMessage("user_not_exist"));
        }
        String refreshToken = userUpdateDTO.getRefreshToken();
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

        BusinessCardAdmin user = new BusinessCardAdmin();
        user.setId(id);
        user.setPassword(SecureUtil.md5(password));
        businessCardAdminService.updateById(user);
        return R.success(true);

    }


    @ApiOperation("重置密码")
    @PostMapping("anno/resetPassword")
    public R<Boolean> resetPassword(@RequestBody @Validated UserResetPasswordDTO userResetPassword) {

        String mobile = userResetPassword.getUserMobile();
        String smsCode = userResetPassword.getSmsCode();
        String password = userResetPassword.getPassword();

        mobile = mobile.trim();

        BusinessCardAdmin user = null;
        try {
            user = businessCardAdminService.getOne(Wraps.<BusinessCardAdmin>lbQ()
                    .eq(BusinessCardAdmin::getUserMobile, EncryptionUtil.encrypt(mobile))
                    .last(" limit 0,1 "));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            throw new BizException(I18nUtils.getMessage("user_not_exist"));
        }
        checkSMS(mobile, smsCode, true);
        user.setPassword(mobile);
        user.setPassword(SecureUtil.md5(password));
        businessCardAdminService.updateById(user);
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



    @ApiOperation("使用手机号登录")
    @PostMapping("anno/aiMobileLogin")
    public R<JSONObject> aiMobileLogin(@RequestBody @Validated BusinessCardAdminLogin cardAdminLogin) {


        String userMobile = cardAdminLogin.getUserMobile();
        String smsCode = cardAdminLogin.getSmsCode();

        String password = cardAdminLogin.getPassword();


        userMobile = userMobile.trim();
        // 验证短信验证码是否正确
        BusinessCardAdmin user = null;
        try {
            user = businessCardAdminService.getOne(Wraps.<BusinessCardAdmin>lbQ()
                    .eq(BusinessCardAdmin::getUserMobile, EncryptionUtil.encrypt(userMobile))
                    .last(" limit 0,1 "));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            throw new BizException(I18nUtils.getMessage("user_not_exist"));
        }
        if (StrUtil.isNotBlank(password)) {
            if (StrUtil.isBlank(user.getPassword())) {
                throw new BizException(I18nUtils.getMessage("password_not_set"));
            }
            if (!SecureUtil.md5(password).equals(user.getPassword())) {
                throw new BizException(I18nUtils.getMessage("password_error"));
            }
        } else {
            checkSMS(userMobile, smsCode, true);
        }
        R<JSONObject> token = null;
        token = oauthApi.createTempToken(user.getId());
        token.getData().put("userType", user.getUserType());
        token.getData().put("userMobile", maskPhoneNumber(userMobile));
        return token;

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

        String phone = smsDto.getPhone();
        // 限制一小时只能发10条短信
        String key = "ai_business_card_count:" + phone;
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
        BoundSetOperations<String, String> operations = redisTemplate.boundSetOps("ai_business_card_:" + phone);
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
            String key = "ai_business_card_:" + phone;
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


}
