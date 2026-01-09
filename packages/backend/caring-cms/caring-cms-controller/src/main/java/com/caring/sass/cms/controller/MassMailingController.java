package com.caring.sass.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.constant.MassMailingMessageStatus;
import com.caring.sass.cms.constant.SendTarget;
import com.caring.sass.cms.dto.*;
import com.caring.sass.cms.entity.MassMailing;
import com.caring.sass.cms.service.MassMailingService;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MassMailingController
 * @Description
 * @Author yangShuai
 * @Date 2021/11/22 11:39
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/massMailing")
@Api(value = "MassMailingController", tags = "群发消息")
public class MassMailingController extends SuperController<MassMailingService, Long, MassMailing, MassMailingPageDto, MassMailingSaveDTO, MassMailingUpdateDto> {

    @Override
    public void query(PageParams<MassMailingPageDto> params, IPage<MassMailing> page, Long defSize) {
        String tenant = BaseContextHandler.getTenant();
        MassMailingPageDto paramsModel = params.getModel();
        LbqWrapper<MassMailing> wrapper = Wraps.lbQ();
        if (StringUtils.isEmpty(tenant)) {
            throw new BizException("租户不存在");
        }
        wrapper.eq(MassMailing::getTenantCode, tenant);
        if (StringUtils.isNotEmptyString(paramsModel.getSendType())) {
            wrapper.eq(MassMailing::getSendType, paramsModel.getSendType());
        }
        if (paramsModel.getMessageStatus() != null) {
            wrapper.eq(MassMailing::getMessageStatus, paramsModel.getMessageStatus());
        }
        if (paramsModel.getMediaTypeEnum() != null) {
            wrapper.eq(MassMailing::getMessageStatus, paramsModel.getMessageStatus());
        }
        wrapper.orderByAsc(MassMailing::getMassSort)
                .orderByDesc(SuperEntity::getCreateTime);
        baseService.page(page, wrapper);
        super.handlerResult(page);
    }

    @Override
    public R<MassMailing> save(@RequestBody MassMailingSaveDTO massMailingSaveDTO) {
        MassMailing mailing = handlerSaveOrUpdate(massMailingSaveDTO);
        String tenant = BaseContextHandler.getTenant();
        mailing.setTenantCode(tenant);
        mailing.setSendTarget(SendTarget.all);
        mailing.setMassSort(4);
        mailing.setMessageStatus(MassMailingMessageStatus.wait_send);
        baseService.save(mailing);
        return R.success(mailing);
    }

    @Override
    public R<MassMailing> update(@RequestBody MassMailingUpdateDto updateDto) {
        MassMailing massMailing = handlerSaveOrUpdate(updateDto);
        massMailing.setId(updateDto.getId());
        baseService.updateById(massMailing);
        return R.success(massMailing);
    }

    @ApiOperation("设置消息定时推送")
    @PutMapping("timingMassMailing")
    public R<Boolean> timingMassMailing(@RequestBody MassMailingTimingDto timingDto) {

        baseService.timingMassMailing(timingDto);
        return R.success(true);
    }


    @ApiOperation("取消发送")
    @PutMapping("cancelTimingMassMailing/{id}")
    public R<Boolean> cancelTimingMassMailing(@PathVariable Long id) {

        baseService.cancelTimingMassMailing(id);
        return R.success(true);
    }

    @ApiOperation("群发消息的详细信息")
    @GetMapping("getDetail/{id}")
    public R<MassMailingVo> getDetail(@PathVariable Long id) {

        MassMailing massMailing = baseService.getById(id);
        ArrayList<MassMailing> objects = new ArrayList<>();
        objects.add(massMailing);
        List<MassMailingVo> mailingVos = baseService.modelToVo(objects);
        MassMailingVo mailingVo = mailingVos.get(0);
        return R.success(mailingVo);
    }


    @ApiOperation("预览群发消息")
    @PostMapping("previewMass")
    public R previewMass(@RequestBody MassMailingPreviewDto previewDto) {

        if (previewDto.getId() == null) {
            return baseService.previewArticleNews(previewDto);
        } else {
            MassMailingUpdateDto mailingUpdateDto = baseService.getDetail(previewDto.getId());
            BeanUtils.copyProperties(mailingUpdateDto, previewDto);
            return baseService.previewArticleNews(previewDto);
        }
    }

    @ApiOperation("发送群发消息")
    @GetMapping("sendMassMailing/{id}")
    public R<Boolean> sendMassMailing(@PathVariable("id") Long id) {
        MassMailing mailing = baseService.getById(id);
        mailing.setSendType("manual");
        baseService.updateById(mailing);
        baseService.sendMassMailing(id);
        return R.success(true);
    }


    @ApiOperation("群发消息的列表对列表处理")
    @PostMapping("pageMassMailing")
    public R<IPage<MassMailingVo>> pageMassMailing(@RequestBody PageParams<MassMailingPageDto> params) {

        IPage<MassMailing> page = params.buildPage();
        this.query(params, page, null);
        List<MassMailing> records = page.getRecords();
        List<MassMailingVo> mailingVos = baseService.modelToVo(records);

        IPage<MassMailingVo> voPage = new Page(page.getCurrent(), page.getSize());
        voPage.setTotal(page.getTotal());
        voPage.setPages(page.getPages());
        voPage.setRecords(mailingVos);
        return R.success(voPage);
    }



    public MassMailing handlerSaveOrUpdate(MassMailingSaveDTO baseDto) {

        List<Long> tagIds = baseDto.getTagIds();
        List<Long> openIds = baseDto.getOpenIds();
        List<Long> articleImageList = baseDto.getArticleImageList();
        List<Long> articleNewsList = baseDto.getArticleNewsList();
        MassMailing massMailing = new MassMailing();
        BeanUtils.copyProperties(baseDto, massMailing);
        baseService.listToJsonString(massMailing, tagIds, openIds, articleImageList, articleNewsList);
        massMailing.setTenantCode(BaseContextHandler.getTenant());
        return massMailing;
    }


    @PostMapping("massCallBack")
    public void massCallBack(@RequestBody MassCallBack massCallBack) {

        baseService.massCallBack(massCallBack);

    }

    /**
     * 发送定时发送的 消息
     */
    @GetMapping("massMailing/send")
    public void massMailing() {
        // 定时任务发送到这里可能有延迟。
        LocalDateTime dateTime = LocalDateTime.now();
        baseService.sendTimingMassMailing(dateTime);

    }



}
