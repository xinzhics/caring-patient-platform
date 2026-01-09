package com.caring.sass.ai.controller.card;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.card.service.BusinessCardMemberRedemptionCodeService;
import com.caring.sass.ai.card.service.BusinessCardOrganService;
import com.caring.sass.ai.card.service.BusinessCardService;
import com.caring.sass.ai.dto.card.BusinessCardMemberRedemptionCodePageDTO;
import com.caring.sass.ai.dto.card.BusinessCardMemberRedemptionCodeSaveDTO;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.card.BusinessCardMemberRedemptionCode;
import com.caring.sass.ai.entity.card.BusinessCardOrgan;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 机构会员兑换码
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessCardMemberRedemptionCode")
@Api(value = "BusinessCardMemberRedemptionCode", tags = "机构会员兑换码")
public class BusinessCardMemberRedemptionCodeController {

    @Autowired
    BusinessCardMemberRedemptionCodeService baseService;

    @Autowired
    BusinessCardService businessCardService;

    @Autowired
    BusinessCardOrganService businessCardOrganService;

    @ApiOperation("创建机构的兑换码")
    @PostMapping("/createRedemptionCode")
    public R<Boolean> createRedemptionCode(@RequestBody @Validated BusinessCardMemberRedemptionCodeSaveDTO businessCardMemberRedemptionCodeSaveDTO) {
        Boolean redemptionCode = baseService.createRedemptionCode(businessCardMemberRedemptionCodeSaveDTO);
        return R.success(redemptionCode);
    }



    @ApiOperation("删除兑换码")
    @DeleteMapping("/deleteRedemptionCode/{id}")
    public R<Boolean> deleteRedemptionCode(@PathVariable Long id) {
        BusinessCardMemberRedemptionCode memberRedemptionCode = new BusinessCardMemberRedemptionCode();
        memberRedemptionCode.setId(id);
        memberRedemptionCode.setDeleteFlag(true);
        baseService.updateById(memberRedemptionCode);
        return R.success(true);
    }


    @ApiOperation("分页查询兑换码")
    @PostMapping("queryRedemptionCode")
    public R<IPage<BusinessCardMemberRedemptionCode>> queryRedemptionCode(@RequestBody @Validated PageParams<BusinessCardMemberRedemptionCodePageDTO> pageParams) {

        IPage<BusinessCardMemberRedemptionCode> builtPage = pageParams.buildPage();
        BusinessCardMemberRedemptionCodePageDTO model = pageParams.getModel();


        LbqWrapper<BusinessCardMemberRedemptionCode> wrapper = Wraps.<BusinessCardMemberRedemptionCode>lbQ()
                .eq(BusinessCardMemberRedemptionCode::getDeleteFlag, false)
                .eq(BusinessCardMemberRedemptionCode::getExchangeStatus, model.getExchangeStatus())
                .eq(BusinessCardMemberRedemptionCode::getRedemptionCode, model.getRedemptionCode())
                .eq(BusinessCardMemberRedemptionCode::getOrganId, model.getOrganId());

        if (model.getExchangeStartTime() != null) {
            wrapper.ge(BusinessCardMemberRedemptionCode::getExchangeTime, model.getExchangeStartTime());
        }
        if (model.getExchangeEndTime() != null) {
            wrapper.le(BusinessCardMemberRedemptionCode::getExchangeTime, model.getExchangeEndTime());
        }

        if (StrUtil.isNotBlank(model.getDoctorName())) {
            wrapper.apply(" user_id in (select c.user_id from m_ai_business_card as c where c.doctor_name like '%"+model.getDoctorName()+"%' ) ");
        }

        wrapper.orderByDesc(SuperEntity::getCreateTime);
        baseService.page(builtPage, wrapper);

        List<BusinessCardMemberRedemptionCode> records = builtPage.getRecords();
        List<Long> userIds = records.stream().map(BusinessCardMemberRedemptionCode::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(userIds)) {
            List<BusinessCard> businessCards = businessCardService.list(Wraps.<BusinessCard>lbQ()
                    .select(SuperEntity::getId,
                            BusinessCard::getUserId,
                            BusinessCard::getDoctorName,
                            BusinessCard::getDoctorAvatar,
                            BusinessCard::getDoctorHospital,
                            BusinessCard::getDoctorDepartment,
                            BusinessCard::getDoctorTitle)
                    .in(BusinessCard::getUserId, userIds));
            if (CollUtil.isNotEmpty(businessCards)) {
                Map<Long, BusinessCard> businessCardMap = businessCards.stream().collect(Collectors.toMap(BusinessCard::getUserId, item -> item, (o1, o2) -> o2));
                records.forEach(item -> {
                    BusinessCard businessCard = businessCardMap.get(item.getUserId());
                    item.setBusinessCard(businessCard);
                });
            }
        }
        List<Long> organId = records.stream().map(BusinessCardMemberRedemptionCode::getOrganId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(organId)) {
            List<BusinessCardOrgan> organList = businessCardOrganService.list(Wraps.<BusinessCardOrgan>lbQ()
                    .in(BusinessCardOrgan::getId, organId));
            if (CollUtil.isNotEmpty(organList)) {
                Map<Long, BusinessCardOrgan> businessCardOrganMap = organList.stream().collect(Collectors.toMap(BusinessCardOrgan::getId, item -> item));
                records.forEach(item -> item.setBusinessCardOrgan(businessCardOrganMap.get(item.getOrganId())));
            }
        }
        return R.success(builtPage);
    }


}
