package com.caring.sass.ai.controller.card;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.card.dao.BusinessCardMapper;
import com.caring.sass.ai.card.service.BusinessCardMemberVersionService;
import com.caring.sass.ai.dto.card.BusinessCardMemberVersionSaveDTO;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.card.BusinessCardMemberVersion;
import com.caring.sass.ai.entity.card.BusinessCardMemberVersionEnum;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 用户的会员版本
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessCardMemberVersion")
@Api(value = "BusinessCardMemberVersion", tags = "用户的会员版本")
public class BusinessCardMemberVersionController {

    @Autowired
    private BusinessCardMemberVersionService businessCardMemberVersionService;

    @Autowired
    private BusinessCardMapper businessCardMapper;

    @ApiOperation("使用兑换码兑换会员")
    @PutMapping("useCodeExchange")
    public R<Boolean> useCodeExchange(@RequestBody @Validated BusinessCardMemberVersionSaveDTO businessCardMemberVersion) {

        Long userId = businessCardMemberVersion.getUserId();
        String redemptionCode = businessCardMemberVersion.getRedemptionCode();
        if (Objects.isNull(userId)) {
            return R.fail("用户ID不能为空");
        }
        if (Objects.isNull(redemptionCode)) {
            return R.fail("兑换码不能为空");
        }
        businessCardMemberVersionService.useCodeExchange(userId, redemptionCode);

        return R.success(true);


    }


    /**
     * 给当前数据库中 已经存在的 名片数据，设置为基础版会员
     * @return
     */

    @ApiModelProperty("初始化已有用户的会员版本为基础版")
    @GetMapping("initUserVersion")
    public R<Boolean> initUserVersion() {

        List<BusinessCard> businessCards = businessCardMapper.selectList(Wraps.<BusinessCard>lbQ());
        if (businessCards.isEmpty()){
            return R.success(true);
        }
        for (BusinessCard card : businessCards) {

            Long userId = card.getUserId();
            if (userId == null) {
                continue;
            }
            int count = businessCardMemberVersionService.count(Wraps.<BusinessCardMemberVersion>lbQ().eq(BusinessCardMemberVersion::getUserId, userId));
            if (count >= 0) {
                continue;
            }
            BusinessCardMemberVersion memberVersion = new BusinessCardMemberVersion();
            memberVersion.setMemberVersion(BusinessCardMemberVersionEnum.BASIC_EDITION);
            memberVersion.setUserId(userId);
            memberVersion.setExpirationDate(card.getCreateTime().plusDays(365));
            businessCardMemberVersionService.save(memberVersion);
        }
        return R.success(true);

    }




    @ApiOperation("查询当前用户的会员版本")
    @GetMapping("query/{userId}")
    public R<BusinessCardMemberVersion> query(@PathVariable Long userId) {


        BusinessCardMemberVersion memberVersion = businessCardMemberVersionService.queryUserVersion(userId);

        return R.success(memberVersion);


    }





}
