package com.caring.sass.user.controller;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.dto.MiniappInfoPageDTO;
import com.caring.sass.user.dto.MiniappInfoSaveDTO;
import com.caring.sass.user.dto.MiniappInfoUpdateDTO;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.MiniappInfo;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.MiniappInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 小程序用户openId关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/miniappInfo")
@Api(value = "MiniappInfo", tags = "小程序用户关联表")
public class MiniappInfoController extends SuperController<MiniappInfoService, Long, MiniappInfo, MiniappInfoPageDTO, MiniappInfoSaveDTO, MiniappInfoUpdateDTO> {


    @Autowired
    DoctorService doctorService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @ApiOperation("使用小程序的openId获取用户信息")
    @GetMapping("findMiniAppUserByOpenId")
    public R<MiniappInfo> findByOpenId(@RequestParam String openId, @RequestParam(name = "appId") String appId) {

        MiniappInfo one = baseService.getOne(Wraps.<MiniappInfo>lbQ()
                .eq(MiniappInfo::getMiniAppOpenId, openId)
                .eq(MiniappInfo::getMiniAppId, appId)
                .last(" limit 0,1 "));
        if (Objects.nonNull(one)) {
            Object o = redisTemplate.boundHashOps(SaasRedisBusinessKey.MINI_APP_USER_SESSION_KEY).get(openId);
            if (Objects.nonNull(o)) {
                one.setSessionKey(o.toString());
            }
            baseService.updateById(one);
        }
        return R.success(one);

    }


    @ApiOperation("查询用户信息通过ID")
    @GetMapping("selectByIdNoTenant/{id}")
    public R<MiniappInfo> selectByIdNoTenant(@PathVariable("id") Long id) {

        MiniappInfo miniappInfo = baseService.selectByIdNoTenant(id);
        return R.success(miniappInfo);

    }


    @ApiOperation("修改用户名称")
    @PostMapping("updateUserName")
    public R<String> updateUserName(@RequestBody MiniappInfo miniappInfo) {

        MiniappInfo info = new MiniappInfo();
        info.setId(miniappInfo.getId());
        if (StrUtil.isBlank(miniappInfo.getUserName()) && StrUtil.isBlank(miniappInfo.getUserAvatar())) {
            return R.success("SUCCESS");
        }
        if (StrUtil.isNotBlank(miniappInfo.getUserName())) {
            info.setUserName(miniappInfo.getUserName());
        }
        if (StrUtil.isNotBlank(miniappInfo.getUserAvatar())) {
            info.setUserAvatar(miniappInfo.getUserAvatar());
        }
        baseService.updateById(info);
        return R.success("SUCCESS");

    }




    @ApiOperation("查询用户是否需要关注提醒 0 需要提醒")
    @GetMapping("getUserRemindSubscriptionMassage")
    public R<Integer> getUserRemindSubscriptionMassage(@RequestParam Long userId) {

        MiniappInfo one = baseService.getById(userId);
        if (Objects.isNull(one)) {
            return R.success(0);
        } else {
            return R.success(one.getRemindSubscriptionMassage());
        }
    }

    @ApiOperation("设置用户已经提醒过关注")
    @PutMapping("setRemindSubscriptionMassageTrueByPhone")
    public R<Boolean> setRemindSubscriptionMassageTrueByPhone(@RequestParam(name = "phoneNumber")
                                                                  @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
                                                                  String phoneNumber) {

        MiniappInfo one = baseService.getOne(Wraps.<MiniappInfo>lbQ().eq(MiniappInfo::getPhoneNumber, phoneNumber).last(" limit 0,1 "));
        if (Objects.isNull(one)) {
            return R.success(true);
        }
        one.setRemindSubscriptionMassage(CommonStatus.YES);
        baseService.updateById(one);
        return R.success(true);
    }

    @ApiOperation("设置用户已经提醒过关注")
    @PutMapping("setRemindSubscriptionMassageTrue")
    public R<Boolean> setRemindSubscriptionMassageTrue(@RequestParam Long id) {

        MiniappInfo miniappInfo = new MiniappInfo();
        miniappInfo.setId(id);
        miniappInfo.setRemindSubscriptionMassage(CommonStatus.YES);
        baseService.updateById(miniappInfo);
        return R.success(true);
    }


    @ApiOperation("创建或者更新微信小程序用户信息(无授权)")
    @PostMapping("anno/createOrUpdateUser")
    public R<MiniappInfo> annoCreateOrUpdateUser(@RequestBody @Validated MiniappInfo miniappInfo) {
        return createOrUpdateUser(miniappInfo);
    }

    @ApiOperation("创建或者更新微信小程序用户信息")
    @PostMapping("createOrUpdateUser")
    public R<MiniappInfo> createOrUpdateUser(@RequestBody @Validated MiniappInfo miniappInfo) {

        MiniappInfo one = baseService.getOne(Wraps.<MiniappInfo>lbQ()
                .eq(MiniappInfo::getMiniAppId, miniappInfo.getMiniAppId())
                .eq(MiniappInfo::getMiniAppOpenId, miniappInfo.getMiniAppOpenId())
                .last(" limit 0,1 "));
        String clientType = miniappInfo.getClientType();
        String phoneNumber = miniappInfo.getPhoneNumber();
        Object o = redisTemplate.boundHashOps(SaasRedisBusinessKey.MINI_APP_USER_SESSION_KEY).get(miniappInfo.getMiniAppOpenId());
        if (Objects.nonNull(o)) {
            miniappInfo.setSessionKey(o.toString());
        } else {
            miniappInfo.setSessionKey(null);
        }
        // 如果是点记的小程序用户登录。直接在这里返回
        if ("know".equals(clientType)) {
            miniappInfo.getId();
            one = baseService.selectByIdNoTenant(miniappInfo.getId());
            if (one == null) {
                baseService.save(miniappInfo);
                return R.success(miniappInfo);
            } else {
                if (Objects.nonNull(o)) {
                    one.setSessionKey(o.toString());
                }
                baseService.updateById(one);
                return R.success(one);
            }

        } else if (StrUtil.isNotEmpty(phoneNumber)) {
            Doctor doctor = doctorService.getByMobile(phoneNumber);
            miniappInfo.setRemindSubscriptionMassage(CommonStatus.NO);
            if (Objects.nonNull(doctor)) {
                miniappInfo.setUserId(doctor.getId());
                miniappInfo.setRoleType(UserType.UCENTER_DOCTOR);
                if (doctor.getWxStatus() != null && doctor.getWxStatus() == 1) {
                    miniappInfo.setRemindSubscriptionMassage(CommonStatus.YES);
                }
            }
        }


        if (Objects.isNull(one)) {
            baseService.save(miniappInfo);
        } else {
            miniappInfo.setId(one.getId());
            baseService.updateById(miniappInfo);
        }
        return R.success(miniappInfo);
    }



}
