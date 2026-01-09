package com.caring.sass.cms.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.constant.StudioCmsType;
import com.caring.sass.cms.dto.ContentPatientCountDTO;
import com.caring.sass.cms.entity.*;
import com.caring.sass.cms.dto.StudioCmsSaveDTO;
import com.caring.sass.cms.dto.StudioCmsUpdateDTO;
import com.caring.sass.cms.dto.StudioCmsPageDTO;
import com.caring.sass.cms.service.StudioCmsService;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.cms.service.StudioCollectService;
import com.caring.sass.cms.service.StudioContentReplyService;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 医生cms详情表
 * </p>
 *
 * @author 杨帅
 * @date 2025-11-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/studioCms")
@Api(value = "StudioCms", tags = "医生cms详情表")
public class StudioCmsController extends SuperController<StudioCmsService, Long, StudioCms, StudioCmsPageDTO, StudioCmsSaveDTO, StudioCmsUpdateDTO> {

    @Autowired
    StudioContentReplyService studioContentReplyService;


    @Autowired
    StudioCollectService studioCollectService;

    @Override
    public R<IPage<StudioCms>> page(PageParams<StudioCmsPageDTO> params) {
        StudioCmsPageDTO model = params.getModel();
        IPage<StudioCms> builtPage = params.buildPage();

        LbqWrapper<StudioCms> eq = Wraps.<StudioCms>lbQ().eq(StudioCms::getDoctorId, model.getDoctorId());
        eq.eq(StudioCms::getCmsType, model.getCmsType());
        eq.eq(StudioCms::getReleaseStatus, model.getReleaseStatus());
        eq.like(StudioCms::getCmsTitle, model.getCmsTitle());
        eq.orderByDesc(StudioCms::getPinToTopSort);
        eq.orderByDesc(StudioCms::getCreateTime);
        baseService.page(builtPage, eq);
        return R.success(builtPage);
    }

    /**
     * 置顶cms
     */
    @ApiOperation(value = "置顶cms")
    @PutMapping("/pinToTop/{cmsId}")
    public R<Void> pinToTop(@PathVariable Long cmsId, @RequestParam Long doctorId) {
        baseService.pinToTop(cmsId, doctorId);
        return R.success(null);
    }


    /**
     * 发布或取消发布
     */
    @ApiOperation(value = "发布或取消发布")
    @PutMapping("/release/{cmsId}")
    public R<Void> release(@PathVariable Long cmsId) {
        baseService.release(cmsId);
        return R.success(null);
    }


    @Override
    public R<StudioCms> get(Long id) {
        StudioCms cms = baseService.getById(id);
        boolean codeCheck = studioCollectService.hasCollect(cms.getId(), getUserId());
        cms.setCollectStatus(codeCheck ? 1 : 0);
        return R.success(cms);
    }

    @ApiOperation(value = "科普创作删除数据")
    @GetMapping("/anno/deleteArticleData/{articleDataId}")
    public R<Void> deleteArticleData(@PathVariable Long articleDataId,
                                     @RequestParam StudioCmsType studioCmsType,
                                     @RequestParam String authToken) {

        if (!authToken.equals("b8774901-7273-44d2-a10c-29c0aafef869")) {
            return R.fail("权限校验失败");
        }
        baseService.deleteArticleData(articleDataId, studioCmsType);
        return R.success(null);
    }



    @ApiOperation("统计用户(默认患者)收藏评论数量")
    @GetMapping("countPatient")
    public R<ContentPatientCountDTO> queryPatientHomeContent(@RequestParam Long userId) {

        // 收藏的文章数量
        int count = studioCollectService.count(Wraps.<StudioCollect>lbQ()
                .eq(StudioCollect::getCollectStatus, 1)
                .eq(StudioCollect::getUserId, userId));
        ContentPatientCountDTO countDTO = new ContentPatientCountDTO();
        countDTO.setCollectCount(count);
        QueryWrapper<StudioContentReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(" count( DISTINCT c_cms_id ) as countNum ")
                .eq("c_replier_id", userId.toString());
        List<Map<String, Object>> maps = studioContentReplyService.listMaps(queryWrapper);
        int replyCount = 0;
        if (CollUtil.isNotEmpty(maps)) {
            Map<String, Object> objectMap = maps.get(0);
            if (CollUtil.isNotEmpty(objectMap)) {
                Object countNum = objectMap.get("countNum");
                if (Objects.nonNull(countNum)) {
                    replyCount = Integer.parseInt(countNum.toString());
                }
            }
        }
        countDTO.setReplyContentCount(replyCount);
        return R.success(countDTO);
    }



}
