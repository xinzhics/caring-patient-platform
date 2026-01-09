package com.caring.sass.ai.controller.ckd;


import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.ckd.server.CkdUserInfoService;
import com.caring.sass.ai.dto.ckd.CkdFreeTimeDto;
import com.caring.sass.ai.dto.ckd.CkdMembershipLevel;
import com.caring.sass.ai.dto.ckd.CkdUserInfoSaveDTO;
import com.caring.sass.ai.dto.ckd.CkdUserMembershipUpdateDto;
import com.caring.sass.ai.entity.ckd.CkdUserInfo;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * CKD用户信息
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ckdUserInfo")
@Api(value = "CkdUserInfo", tags = "CKD用户信息")
public class CkdUserInfoController  {

    @Autowired
    CkdUserInfoService ckdUserInfoService;


    @ApiOperation("查询用户信息")
    @GetMapping("getByOpenId")
    public R<CkdUserInfo> getByOpenId(@RequestParam String openId) {

        CkdUserInfo userInfo = ckdUserInfoService.getByOpenId(openId);
        return R.success(userInfo);
    }

    // 查询用户是否已经选择的会员
    @ApiOperation("设置用户为免费会员（只限一次）")
    @PutMapping("setFree")
    public R<CkdUserInfo> setFree(@RequestParam String openId) {

        CkdUserInfo userInfo = ckdUserInfoService.getByOpenId(openId);
        if (userInfo.getMembershipLevel() == null) {
            userInfo.setMembershipLevel(CkdMembershipLevel.FREE);
            userInfo.setExpirationDate(LocalDateTime.now().plusDays(7));
        } else {
            throw new BizException("没有免费机会了。");
        }
        ckdUserInfoService.updateById(userInfo);
        return R.success(userInfo);
    }

    @ApiOperation("设置用户的会员")
    @PutMapping("setMembershipLevel")
    public R<Boolean> setMembershipLevel(@RequestBody @Validated CkdUserMembershipUpdateDto membershipUpdateDto) {
        ckdUserInfoService.setMembershipLevel(membershipUpdateDto);
        return R.success(true);
    }

    @ApiOperation("查询用户信息")
    @GetMapping("getUserInfoByName")
    public R<List<CkdUserInfo>> getUserInfoByName(@RequestParam(required = true) String name) {
        if (StrUtil.isBlank(name)) {
            return R.success(null);
        }
        List<CkdUserInfo> userInfos = ckdUserInfoService.list(Wraps.<CkdUserInfo>lbQ().eq(CkdUserInfo::getNickname, name));
        return R.success(userInfos);
    }




    @ApiOperation("提交用户的基本信息")
    @PostMapping("submitUserInfo")
    public R<CkdUserInfo> submitUserInfo(@RequestBody @Validated CkdUserInfoSaveDTO ckdUserInfoSaveDTO) {

        CkdUserInfo userInfo = ckdUserInfoService.getByOpenId(ckdUserInfoSaveDTO.getOpenId());
        BeanUtils.copyProperties(ckdUserInfoSaveDTO, userInfo);
        ckdUserInfoService.updateById(userInfo);
        return R.success(userInfo);
    }

    @ApiOperation("修改用户粉丝的免费时长")
    @PutMapping("updateFreeTime")
    public R<Boolean> updateFreeTime(@RequestBody @Validated CkdFreeTimeDto ckdFreeTimeDto) {

        ckdUserInfoService.updateFreeTime(ckdFreeTimeDto);
        return R.success(true);
    }



}
