package com.caring.sass.ai.controller.card;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.card.service.BusinessCardAdminService;
import com.caring.sass.ai.card.service.BusinessCardOrganService;
import com.caring.sass.ai.card.service.BusinessCardService;
import com.caring.sass.ai.card.service.BusinessCardUseDayStatisticsService;
import com.caring.sass.ai.dto.BusinessCardUseDayStatisticsQueryDto;
import com.caring.sass.ai.dto.BusinessCardUseDayStatisticsResultDto;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.card.BusinessCardAdmin;
import com.caring.sass.ai.entity.card.BusinessCardOrgan;
import com.caring.sass.ai.entity.card.BusinessCardUseDayStatistics;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 名片使用数据日统计
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessCardUseDayStatistics")
@Api(value = "BusinessCardUseDayStatistics", tags = "名片使用数据日统计")
public class BusinessCardUseDayStatisticsController  {


    @Autowired
    BusinessCardAdminService businessCardAdminService;

    @Autowired
    BusinessCardService businessCardService;

    @Autowired
    BusinessCardOrganService businessCardOrganService;

    @Autowired
    BusinessCardUseDayStatisticsService businessCardUseDayStatisticsService;

    /**
     * 统计当前用户可见的数据汇总
     * @param businessCardUseDayStatisticsQueryDto
     * @return
     */
    @ApiOperation("统计当前用户可见的数据汇总")
    @PostMapping("getBusinessCardUseDayStatistics")
    public R<BusinessCardUseDayStatisticsResultDto> getBusinessCardUseDayStatistics(@RequestBody @Validated BusinessCardUseDayStatisticsQueryDto businessCardUseDayStatisticsQueryDto) {

        Long userId = BaseContextHandler.getUserId();
        BusinessCardAdmin businessCardAdmin = businessCardAdminService.getById(userId);
        Long organId = null;
        if (Objects.isNull(businessCardAdmin)) {
            throw new BizException("管理员不存在");
        }
        organId = businessCardAdmin.getOrganId();
        // 统计本机构下的卡片总数

        LbqWrapper<BusinessCard> wrapper = Wraps.<BusinessCard>lbQ();


        QueryWrap<BusinessCardUseDayStatistics> wrapAllPush = Wraps.<BusinessCardUseDayStatistics>q()
                .select(
                        "sum(people_of_views) as people_of_views",
                        "sum(number_of_views) as number_of_views",
                        "sum(forwarding_frequency) as forwarding_frequency",
                        "sum(ai_dialogue_number_count) as ai_dialogue_number_count",
                        "sum(ai_dialogue_click_count) as ai_dialogue_click_count");

        if (organId != null) {
            wrapper.eq(BusinessCard::getOrganId, organId);
            wrapAllPush.eq("organ_id", organId);
        }
        if (StrUtil.isNotBlank(businessCardUseDayStatisticsQueryDto.getDoctorName())) {
            wrapper.like(BusinessCard::getDoctorName, businessCardUseDayStatisticsQueryDto.getDoctorName());
            wrapAllPush.like("doctor_name", businessCardUseDayStatisticsQueryDto.getDoctorName());
        }
        if (StrUtil.isNotBlank(businessCardUseDayStatisticsQueryDto.getHospitalName())) {
            wrapper.like(BusinessCard::getDoctorHospital, businessCardUseDayStatisticsQueryDto.getHospitalName());
            wrapAllPush.like("hospital_name", businessCardUseDayStatisticsQueryDto.getHospitalName());
        }
        if (StrUtil.isNotBlank(businessCardUseDayStatisticsQueryDto.getDepartmentName())) {
            wrapper.like(BusinessCard::getDoctorDepartment, businessCardUseDayStatisticsQueryDto.getDepartmentName());
            wrapAllPush.like("department_name", businessCardUseDayStatisticsQueryDto.getDepartmentName());
        }
        if (businessCardUseDayStatisticsQueryDto.getStartStatisticsDate() != null) {
            wrapper.ge(BusinessCard::getCreateTime, LocalDateTime.of(businessCardUseDayStatisticsQueryDto.getStartStatisticsDate(), LocalTime.MIN));
            wrapAllPush.gt("use_date", businessCardUseDayStatisticsQueryDto.getStartStatisticsDate());
        }
        if (businessCardUseDayStatisticsQueryDto.getEndStatisticsDate() != null) {
            wrapper.le(BusinessCard::getCreateTime, LocalDateTime.of(businessCardUseDayStatisticsQueryDto.getEndStatisticsDate(), LocalTime.MAX));
            wrapAllPush.lt("use_date", businessCardUseDayStatisticsQueryDto.getEndStatisticsDate());
        }
        BusinessCardUseDayStatisticsResultDto resultDto = new BusinessCardUseDayStatisticsResultDto();
        int count = businessCardService.count(wrapper);
        resultDto.setCardCount(count);

        List<Map<String, Object>> mapList = businessCardUseDayStatisticsService.listMaps(wrapAllPush);
        for (Map<String, Object> objectMap : mapList) {
            if (objectMap == null) {
                continue;
            }
            Object peopleOfViews = objectMap.get("people_of_views");
            if (peopleOfViews != null) {
                resultDto.setPeopleOfViews(Integer.parseInt(peopleOfViews.toString()));
            }
            Object numberOfViews = objectMap.get("number_of_views");
            if (numberOfViews != null) {
                resultDto.setNumberOfViews(Integer.parseInt(numberOfViews.toString()));
            }
            Object forwardingFrequency = objectMap.get("forwarding_frequency");
            if (forwardingFrequency != null) {
                resultDto.setForwardingFrequency(Integer.parseInt(forwardingFrequency.toString()));
            }
            Object aiDialogueNumberCount = objectMap.get("ai_dialogue_number_count");
            if (aiDialogueNumberCount != null) {
                resultDto.setAiDialogueNumberCount(Integer.parseInt(aiDialogueNumberCount.toString()));
            }
            Object aiDialogueClickCount = objectMap.get("ai_dialogue_click_count");
            if (aiDialogueClickCount != null) {
                resultDto.setAiDialogueClickCount(Integer.parseInt(aiDialogueClickCount.toString()));
            }
        }
        return R.success(resultDto);

    }

    @ApiOperation("初始化日常统计数据")
    @GetMapping("initUserStatistics")
    public R<Boolean> initUserStatistics() {

        businessCardUseDayStatisticsService.initUserStatistics();
        return R.success(true);
    }


    @ApiOperation("分页列表")
    @PostMapping("pageBusinessCardUseDayStatistics")
    public R<IPage<BusinessCardUseDayStatistics>> pageBusinessCardUseDayStatistics(@RequestBody @Validated PageParams<BusinessCardUseDayStatisticsQueryDto> pageParams) {

        IPage<BusinessCardUseDayStatistics> builtPage = pageParams.buildPage();
        BusinessCardUseDayStatisticsQueryDto paramsModel = pageParams.getModel();
        long current = pageParams.getCurrent();
        long size = pageParams.getSize();
        String sortClo = paramsModel.getSortClo();
        String sortType = paramsModel.getSortType();

        // SELECT SUM(people_of_views) as peoper_of_views, SUM(number_of_views) FROM `m_ai_business_card_use_day_statistics`
        // where organ_id = '1' GROUP BY business_card_id ORDER BY peoper_of_views desc limit 0, 10
        Long userId = BaseContextHandler.getUserId();
        BusinessCardAdmin businessCardAdmin = businessCardAdminService.getById(userId);
        Long organId = null;
        if (Objects.isNull(businessCardAdmin)) {
            throw new BizException("管理员不存在");
        }
        organId = businessCardAdmin.getOrganId();

        QueryWrap<BusinessCardUseDayStatistics> countWrapper = Wraps.<BusinessCardUseDayStatistics>q();
        countWrapper.select("count(distinct business_card_id) countNumber");
        QueryWrap<BusinessCardUseDayStatistics> wrapAllPush = Wraps.<BusinessCardUseDayStatistics>q()
                .select(
                        "business_card_id",
                        "sum(people_of_views) as peopleOfViews",
                        "sum(number_of_views) as numberOfViews",
                        "sum(forwarding_frequency) as forwardingFrequency",
                        "sum(ai_dialogue_number_count) as aiDialogueNumberCount",
                        "sum(ai_dialogue_click_count) as aiDialogueClickCount");
        if (organId != null) {
            wrapAllPush.eq("organ_id", organId);
            countWrapper.eq("organ_id", organId);
        }
        if (StrUtil.isNotBlank(paramsModel.getDoctorName())) {
            wrapAllPush.like("doctor_name", paramsModel.getDoctorName());
            countWrapper.like("doctor_name", paramsModel.getDoctorName());
        }
        if (StrUtil.isNotBlank(paramsModel.getHospitalName())) {
            wrapAllPush.like("hospital_name", paramsModel.getHospitalName());
            countWrapper.like("hospital_name", paramsModel.getHospitalName());
        }
        if (StrUtil.isNotBlank(paramsModel.getDepartmentName())) {
            wrapAllPush.like("department_name", paramsModel.getDepartmentName());
            countWrapper.like("department_name", paramsModel.getDepartmentName());
        }
        if (paramsModel.getStartStatisticsDate() != null) {
            wrapAllPush.gt("use_date", paramsModel.getStartStatisticsDate());
            countWrapper.gt("use_date", paramsModel.getStartStatisticsDate());
        }
        if (paramsModel.getEndStatisticsDate() != null) {
            wrapAllPush.lt("use_date", paramsModel.getEndStatisticsDate());
            countWrapper.lt("use_date", paramsModel.getEndStatisticsDate());
        }
        if (StrUtil.isNotBlank(sortClo) && StrUtil.isNotBlank(sortType)) {
            if (sortType.equals("desc")) {
                wrapAllPush.orderByDesc(sortClo);
            } else {
                wrapAllPush.orderByAsc(sortClo);
            }
            wrapAllPush.orderByDesc("business_card_id");
        } else {
            wrapAllPush.orderByDesc("number_of_views");
        }
        wrapAllPush.groupBy("business_card_id");
        List<Map<String, Object>> countMaps = businessCardUseDayStatisticsService.listMaps(countWrapper);
        int count = 0;
        if (!countMaps.isEmpty()) {
            Map<String, Object> objectMap = countMaps.get(0);
            Object object = objectMap.get("countNumber");
            if (object != null) {
                count = Integer.parseInt(object.toString());
            }
        }
        List<BusinessCardUseDayStatistics> statisticsList = new ArrayList<>();
        int index = (int) ((current - 1) * size);
        if (count > 0 && count >= ((current - 1) * size)) {
            wrapAllPush.last("limit " + index + ", " + size);
            List<Map<String, Object>> mapList = businessCardUseDayStatisticsService.listMaps(wrapAllPush);
            for (Map<String, Object> objectMap : mapList) {
                if (objectMap == null) {
                    continue;
                }
                BusinessCardUseDayStatistics statistics = BusinessCardUseDayStatistics.builder()
                        .peopleOfViews(objectMap.get("peopleOfViews") == null ? 0 : Integer.parseInt(objectMap.get("peopleOfViews").toString()))
                        .numberOfViews(objectMap.get("numberOfViews") == null ? 0 : Integer.parseInt(objectMap.get("numberOfViews").toString()))
                        .forwardingFrequency(objectMap.get("forwardingFrequency") == null ? 0 : Integer.parseInt(objectMap.get("forwardingFrequency").toString()))
                        .aiDialogueNumberCount(objectMap.get("aiDialogueNumberCount") == null ? 0 : Integer.parseInt(objectMap.get("aiDialogueNumberCount").toString()))
                        .aiDialogueClickCount(objectMap.get("aiDialogueClickCount") == null ? 0 : Integer.parseInt(objectMap.get("aiDialogueClickCount").toString()))
                        .businessCardId(objectMap.get("business_card_id") == null ? null : Long.parseLong(objectMap.get("business_card_id").toString()))
                        .build();
                statisticsList.add(statistics);
            }
            builtPage.setRecords(statisticsList);
            if (CollUtil.isNotEmpty(statisticsList)) {
                List<Long> businessCardIds = statisticsList.stream().map(BusinessCardUseDayStatistics::getBusinessCardId).collect(Collectors.toList());
                List<BusinessCard> businessCards = businessCardService.list(Wraps.<BusinessCard>lbQ().in(BusinessCard::getId, businessCardIds));
                List<Long> organIds = businessCards.stream().map(BusinessCard::getOrganId).filter(Objects::nonNull).collect(Collectors.toList());
                Map<Long, BusinessCardOrgan> cardOrganMap = new HashMap<>();
                if (CollUtil.isNotEmpty(organIds)) {
                    List<BusinessCardOrgan> organList = businessCardOrganService.list(Wraps.<BusinessCardOrgan>lbQ().in(BusinessCardOrgan::getId, organIds));
                    cardOrganMap = organList.stream().collect(Collectors.toMap(BusinessCardOrgan::getId, item -> item));
                }

                Map<Long, BusinessCard> businessCardMap = businessCards.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
                for (BusinessCardUseDayStatistics statistics : statisticsList) {
                    BusinessCard businessCard = businessCardMap.get(statistics.getBusinessCardId());
                    statistics.setDepartmentName(businessCard.getDoctorDepartment());
                    statistics.setHospitalName(businessCard.getDoctorHospital());
                    statistics.setDoctorName(businessCard.getDoctorName());
                    statistics.setOrganId(businessCard.getOrganId());
                    statistics.setBusinessQrCode(businessCard.getBusinessQrCode());
                    statistics.setActivationTime(businessCard.getActivationTime());
                    if (statistics.getOrganId() != null) {
                        BusinessCardOrgan cardOrgan = cardOrganMap.get(statistics.getOrganId());
                        if (Objects.nonNull(cardOrgan)) {
                            statistics.setOrganName(cardOrgan.getOrganName());
                        }
                    }
                }
            }
        } else {
            builtPage.setRecords(new ArrayList<>());
        }
        if (count == 0) {
            builtPage.setPages(0);
        } else {
            long pages = count / size;
            if (count % size != 0) {
                pages++;
            }
            builtPage.setPages(pages);
        }
        builtPage.setCurrent(current);
        builtPage.setSize(size);
        builtPage.setTotal(count);
        return R.success(builtPage);

    }




}
