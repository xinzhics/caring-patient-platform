package com.caring.sass.ai.controller.card;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.ai.card.dao.BusinessCardHumanLimitMapper;
import com.caring.sass.ai.card.dao.BusinessCardStudioMapper;
import com.caring.sass.ai.card.service.BusinessCardDiagramResultService;
import com.caring.sass.ai.card.service.BusinessCardMemberVersionService;
import com.caring.sass.ai.card.service.BusinessCardService;
import com.caring.sass.ai.dto.BusinessCardSaveDTO;
import com.caring.sass.ai.dto.BusinessCardUpdateDTO;
import com.caring.sass.ai.dto.userbiz.AiUserBizBo;
import com.caring.sass.ai.entity.UserJoin;
import com.caring.sass.ai.entity.card.*;
import com.caring.sass.ai.service.UserJoinService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * AI名片
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessCard")
@Api(value = "BusinessCard", tags = "AI名片")
public class BusinessCardController {

    @Autowired
    BusinessCardStudioMapper businessCardStudioMapper;

    @Autowired
    BusinessCardService baseService;

    @Autowired
    BusinessCardDiagramResultService businessCardDiagramResultService;

    @Autowired
    BusinessCardMemberVersionService businessCardMemberVersionService;


    @ApiOperation("获取医生名片")
    @GetMapping("anno/get/{cardId}")
    public R<BusinessCard> getBusinessCard(@PathVariable("cardId") Long cardId) {

        BusinessCard businessCard = baseService.getById(cardId);
        if (Objects.isNull(businessCard)) {
            return R.fail("名片不存在");
        }
        baseService.setDoctorStudio(businessCard);
        return R.success(businessCard);

    }


    @ApiOperation("初始化二维码")
    @GetMapping("initQrCode")
    public R<Boolean> initQrCode() {

        baseService.initQrCode();
        return R.success(true);
    }


    @ApiOperation("生成医生的分享码")
    @GetMapping("initBusinessQrCode")
    public R<Boolean> initBusinessQrCode(@RequestParam Long id) {

        baseService.initQrCode(id);
        return R.success(true);
    }





    @ApiOperation("获取我的医生名片")
    @GetMapping("getMyCard")
    public R<BusinessCard> getMyCard() {

        Long userId = BaseContextHandler.getUserId();
        if (Objects.isNull(userId)) {
            return R.fail("请登录");
        }
        BusinessCard businessCard = baseService.getOne(Wraps.<BusinessCard>lbQ().eq(BusinessCard::getUserId, userId).last(" limit 0, 1 "));
        if (Objects.isNull(businessCard)) {
            return R.fail("您还没有名片");
        }
        baseService.setDoctorStudio(businessCard);
        return R.success(businessCard);

    }


    @ApiOperation("使用名称匹配医生名片")
    @GetMapping("queryCardByDoctorName")
    public R<Long> queryCardByDoctorName(@RequestParam("doctorName") String doctorName) {

        // 查询没有被关联的预制数据中是否有当前名称的医生。
        BusinessCard businessCard = baseService.getOne(Wraps.<BusinessCard>lbQ()
                .select(SuperEntity::getId,BusinessCard::getDoctorName)
                .eq(BusinessCard::getDoctorName, doctorName)
                .isNull(BusinessCard::getUserId)
                .last(" limit 0, 1 "));
        if (Objects.nonNull(businessCard)) {
            return R.success(businessCard.getId());
        } else {
            return R.success(null);
        }

    }


    @ApiOperation("绑定名片到当前用户")
    @PutMapping("binding/data")
    public R<BusinessCard> bindingData(@RequestParam Long cardId) {

        BusinessCard businessCard = baseService.getById(cardId);
        if (Objects.isNull(businessCard)) {
            return R.fail("名片不存在");
        }
        if (Objects.nonNull(businessCard.getUserId())) {
            return R.fail("名片已被绑定");
        }
        Long userId = BaseContextHandler.getUserId();
        businessCard.setUserId(userId);
        businessCard.setActivationTime(LocalDateTime.now());
        baseService.updateById(businessCard);
        baseService.setDoctorStudio(businessCard);

        /**
         * 使用账号绑定已有名片时，赠送一个基础会员
         */
        businessCardMemberVersionService.setBusinessCardUserMemberVersion(userId, BusinessCardMemberVersionEnum.BASIC_EDITION);
        return R.success(businessCard);

    }





    @ApiOperation("修改名片内容")
    @SysLog("修改医生AI名片")
    @PutMapping("updateBusinessCard")
    @Transactional
    public R<BusinessCard> updateBusinessCard(@RequestBody @Validated BusinessCardUpdateDTO businessCardUpdateDTO) {

        Long id = businessCardUpdateDTO.getId();
        BusinessCard businessCard = baseService.getById(id);
        if (Objects.isNull(businessCard)) {
            return R.fail("名片不存在");
        }
        List<String> doctorStudioList = businessCardUpdateDTO.getDoctorStudioList();
        List<BusinessCardStudio> cardStudios = businessCardUpdateDTO.getStudios();
        BeanUtils.copyProperties(businessCardUpdateDTO, businessCard);

        baseService.checkVideoInfo(businessCard);

        baseService.updateById(businessCard);
        // 删除工作室
        businessCardStudioMapper.delete(Wraps.<BusinessCardStudio>lbQ().eq(BusinessCardStudio::getBusinessCard, id));
        if (CollUtil.isNotEmpty(cardStudios)) {
            cardStudios.forEach(item -> item.setBusinessCard(id));
            businessCardStudioMapper.insertBatchSomeColumn(cardStudios);
        } else if (CollUtil.isNotEmpty(doctorStudioList)) {
            List<BusinessCardStudio> studios = new ArrayList<>();
            doctorStudioList.forEach(item -> {
                studios.add(BusinessCardStudio.builder().doctorStudio(item).businessCard(id).build());
            });
            businessCardStudioMapper.insertBatchSomeColumn(studios);
        }

        businessCardDiagramResultService.updateHistory(businessCard.getUserId(), businessCard.getDoctorAvatar());
        businessCard.setDoctorStudioList(doctorStudioList);

        UserJoin userJoin = userJoinService.getOne(Wraps.<UserJoin>lbQ()
                .eq(UserJoin::getBusinessCardId, id));
        if (Objects.nonNull(userJoin)) {
            AiUserBizBo aiUserBizBo = new AiUserBizBo();
            aiUserBizBo.setUserId(userJoin.getAiKnowUserId());
            aiUserBizBo.setUserName(businessCard.getDoctorName());
            aiUserBizBo.setWorkUnit(businessCard.getDoctorHospital());
            aiUserBizBo.setDepartment(businessCard.getDoctorDepartment());
            aiUserBizBo.setDoctorTitle(businessCard.getDoctorTitle());
            aiUserBizBo.setPersonalProfile(businessCard.getDoctorPersonal());
            aiUserBizBo.setGreetingVideo(businessCard.getDoctorMetaHuman());
            aiUserBizBo.setGreetingVideoCover(businessCard.getDoctorMetaHumanPoster());
            aiUserBizBo.setRealHumanAvatar(businessCard.getDoctorAvatar());
            aiUserBizBo.setSpecialty(businessCard.getDoctorBeGoodAt());
            if (userJoin.getAiKnowUserId() != null) {
                userJoinService.updateKnowledgeUser(aiUserBizBo);
            }

            if (userJoin.getAiStudioDoctorId() != null) {
                userJoinService.updateAiStudioDoctor(userJoin.getAiStudioTenantId(), aiUserBizBo, userJoin.getAiStudioDoctorId());
            }
        }


        return R.success(businessCard);

    }

    @Autowired
    UserJoinService userJoinService;


    @ApiOperation("创建AI医生名片")
    @SysLog("创建AI医生名片")
    @PutMapping("createBusinessCard")
    @Transactional
    public R<BusinessCard> createBusinessCard(@RequestBody @Validated BusinessCardSaveDTO businessCardSaveDTO) {

        BusinessCard card = new BusinessCard();
        Long userId = BaseContextHandler.getUserId();
        if (Objects.isNull(userId)) {
            return R.fail("请登录");
        }
        int count = baseService.count(Wraps.<BusinessCard>lbQ().eq(BusinessCard::getUserId, userId));
        if (count > 0) {
            return R.fail("用户名片已存在");
        }
        List<String> doctorStudioList = businessCardSaveDTO.getDoctorStudioList();
        List<BusinessCardStudio> cardStudios = businessCardSaveDTO.getStudios();

        BeanUtils.copyProperties(businessCardSaveDTO, card);
        card.setUserId(userId);
        card.setHasDoctorStudio(CommonStatus.NO);
        card.setCreateButtonStatus(CommonStatus.NO);

        // 检查用户是否购买了会员
        BusinessCardMemberVersion memberVersion = businessCardMemberVersionService.queryUserVersion(userId);
        if (Objects.nonNull(memberVersion)) {
            card.setMemberVersion(memberVersion.getMemberVersion());
        }

        // 校验视频号信息
        baseService.checkVideoInfo(card);
        baseService.save(card);
        Long cardId = card.getId();
        if (CollUtil.isNotEmpty(cardStudios)) {
            for (BusinessCardStudio studio : cardStudios) {
                studio.setBusinessCard(cardId);
            }
            businessCardStudioMapper.insertBatchSomeColumn(cardStudios);
        } else if (CollUtil.isNotEmpty(doctorStudioList)) {
            List<BusinessCardStudio> studios = new ArrayList<>();
            doctorStudioList.forEach(item -> {
                studios.add(BusinessCardStudio.builder().doctorStudio(item).businessCard(cardId).build());
            });
            businessCardStudioMapper.insertBatchSomeColumn(studios);
        }
        businessCardDiagramResultService.updateHistory(userId, businessCardSaveDTO.getDoctorAvatar());
        card.setDoctorStudioList(doctorStudioList);
        return R.success(card);

    }







}
