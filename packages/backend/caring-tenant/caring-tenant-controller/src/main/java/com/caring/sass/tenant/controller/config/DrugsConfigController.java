package com.caring.sass.tenant.controller.config;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.api.CustomCommendDrugsApi;
import com.caring.sass.nursing.api.CustomDrugsCategoryApi;
import com.caring.sass.nursing.api.SysDrugApi;
import com.caring.sass.nursing.dto.drugs.CustomCommendDrugsPageDTO;
import com.caring.sass.nursing.dto.drugs.CustomCommendDrugsUpdateDTO;
import com.caring.sass.nursing.entity.drugs.CustomCommendDrugs;
import com.caring.sass.nursing.entity.drugs.CustomDrugsCategory;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import com.caring.sass.tenant.dto.config.RecommendDrugsDTO;
import com.caring.sass.tenant.dto.config.RecommendDrugsUpsertDTO;
import com.caring.sass.tenant.dto.config.UpdateBuyDrugSwitchDTO;
import com.caring.sass.tenant.dto.config.UpdateDrugSwitchDTO;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.dto.guide.RegGuideSaveDTO;
import com.caring.sass.wx.entity.guide.RegGuide;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 项目药品配置
 *
 * @author leizhi
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/drugsConfig")
@Api(value = "DrugsConfig", tags = "项目药品配置")
public class DrugsConfigController {

    private final SysDrugApi sysDrugApi;
    private final CustomCommendDrugsApi commendDrugsApi;
    private final CustomDrugsCategoryApi customDrugsCategoryApi;
    private final GuideApi guideApi;

    public DrugsConfigController(SysDrugApi sysDrugApi, CustomCommendDrugsApi commendDrugsApi, CustomDrugsCategoryApi customDrugsCategoryApi, GuideApi guideApi) {
        this.sysDrugApi = sysDrugApi;
        this.commendDrugsApi = commendDrugsApi;
        this.customDrugsCategoryApi = customDrugsCategoryApi;
        this.guideApi = guideApi;
    }

    @ApiOperation("根据名称模糊查询药品列表")
    @GetMapping("querySysDrugs")
    public R<List<SysDrugs>> querySysDrugs(@RequestParam(value = "drugName") String drugName) {
        List<SysDrugs> sysDrugs = new ArrayList<>();
        if (StrUtil.isBlank(drugName)) {
            return R.success(sysDrugs);
        }
        // 调用用药接口查询信息
        R<List<SysDrugs>> r = sysDrugApi.query(SysDrugs.builder().name(drugName).build());
        if (r.getIsError()) {
            return R.success(sysDrugs);
        }
        return R.success(r.getData());
    }

    @ApiOperation("配置推荐用药")
    @PostMapping(value = "addRecommendDrugs/{code}")
    public R<Boolean> addRecommendDrugs(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                        @RequestParam(value = "drugId") @NotNull(message = "药品id不为空") Long drugId) {
        R<SysDrugs> drugsR = sysDrugApi.get(drugId);
        if (drugsR.getIsError()) {
            return R.fail("不存在该药品");
        }
        // 配置项目推荐用药
        BaseContextHandler.setTenant(code);
        return commendDrugsApi.addRecommendDrugs(drugId);
    }

    /**
     * 查询推荐用药
     */
    @ApiOperation("查询推荐用药")
    @GetMapping(value = "listRecommendDrugs/{code}")
    public R<List<RecommendDrugsDTO>> listRecommendDrugs(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        List<RecommendDrugsDTO> recommendDrugsDTOS = new ArrayList<>();
        BaseContextHandler.setTenant(code);
        // 查询项目自定义推荐用药
        PageParams<CustomCommendDrugsPageDTO> pagesQ = new PageParams<>();
        pagesQ.setCurrent(1L);
        pagesQ.setSize(1000L);
        pagesQ.setSort("order_");
        pagesQ.setModel(CustomCommendDrugsPageDTO.builder().build());
        R<IPage<CustomCommendDrugs>> pagesR = commendDrugsApi.page(pagesQ);
        if (pagesR.getIsError()) {
            return R.success(recommendDrugsDTOS);
        }
        // 查询药品详情
        List<CustomCommendDrugs> customCommendDrugs = pagesR.getData().getRecords();
        List<Long> drugIds = new ArrayList<>();
        for (CustomCommendDrugs c : customCommendDrugs) {
            drugIds.add(c.getDrugsId());
        }
        if (CollUtil.isEmpty(drugIds)) {
            return R.success(recommendDrugsDTOS);
        }
        R<List<SysDrugs>> drugsR = sysDrugApi.listByIds(drugIds);
        if (drugsR.getIsError()) {
            return R.success(recommendDrugsDTOS);
        }
        // 拼接药品信息
        List<SysDrugs> drugs = drugsR.getData();
        for (CustomCommendDrugs customCommendDrug : customCommendDrugs) {
            Long drugsId = customCommendDrug.getDrugsId();
            Integer order = customCommendDrug.getOrder();
            Long id = customCommendDrug.getId();
            RecommendDrugsDTO recommendDrugsDTO = RecommendDrugsDTO.builder()
                    .id(id).order(order).drugId(drugsId)
                    .build();
            // 查询是哪个药品
            List<SysDrugs> filterDrugs = drugs.stream().filter(d -> Objects.equals(d.getId(), drugsId)).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(filterDrugs)) {
                SysDrugs d = filterDrugs.get(0);
                BeanUtil.copyProperties(d, recommendDrugsDTO);
            }
            recommendDrugsDTOS.add(recommendDrugsDTO);
        }
        return R.success(recommendDrugsDTOS);
    }

    /**
     * 删除推荐用药
     */
    @ApiOperation("删除推荐用药")
    @GetMapping(value = "delRecommendDrugs/{code}")
    public R<Boolean> delRecommendDrugs(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                        @RequestParam(value = "id") Long id) {
        BaseContextHandler.setTenant(code);
        commendDrugsApi.delRecommendDrugs(id);
        return R.success();
    }

    /**
     * 修改推荐用药序号
     */
    @ApiOperation("修改推荐用药序号")
    @PutMapping(value = "updateRecommendDrugs/{code}")
    public R<Boolean> updateRecommendDrugs(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                           @RequestBody RecommendDrugsUpsertDTO recommendDrugsUpsertDTO) {
        BaseContextHandler.setTenant(code);
        Long id = recommendDrugsUpsertDTO.getId();
        Integer order = recommendDrugsUpsertDTO.getOrder();
        BizAssert.notNull(id);
        commendDrugsApi.update(CustomCommendDrugsUpdateDTO.builder()
                .id(id).order(order).build());
        return R.success();
    }

    /**
     * 查询推荐的药品类别id
     */
    @ApiOperation("查询推荐的药品类别id")
    @GetMapping(value = "queryRecommendDrugCategory/{code}")
    public R<List<Long>> queryRecommendDrugCategory(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        List<Long> ids = new ArrayList<>();
        R<List<CustomDrugsCategory>> r = customDrugsCategoryApi.query(CustomDrugsCategory.builder().build());
        if (r.getIsError()) {
            return R.success(ids);
        }
        for (CustomDrugsCategory datum : r.getData()) {
            ids.add(datum.getCategoryId());
        }
        return R.success(ids);
    }

    /**
     * 修改推荐用药类别
     */
    @ApiOperation("修改推荐用药类别")
    @PutMapping(value = "updateRecommendDrugCategory/{code}")
    public R<Boolean> updateRecommendDrugCategory(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                                  @RequestParam("ids") List<Long> ids) {
        BaseContextHandler.setTenant(code);
        return customDrugsCategoryApi.updateTenantCustomDrugsCategory(ids);
    }

    /**
     * 修改用药开关
     */
    @ApiOperation("修改用药开关")
    @PutMapping(value = "updateDrugSwitch/{code}")
    public R<Boolean> updateDrugSwitch(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                       @RequestBody UpdateDrugSwitchDTO updateDrugSwitchDTO) {
        BaseContextHandler.setTenant(code);
        RegGuideSaveDTO re = RegGuideSaveDTO.builder().build();
        RegGuide guide = guideApi.getGuide().getData();
        if (guide != null) {
            BeanUtil.copyProperties(guide, re);
        }
        re.setHasShowRecommendDrugs(updateDrugSwitchDTO.getHasShowRecommendDrugs());
        re.setHasFillDrugs(updateDrugSwitchDTO.getHasFillDrugs());
        guideApi.upsertByCode(re);
        return R.success();
    }

    /**
     * 开启购药跳转
     */
    @ApiOperation("开启购药跳转")
    @PutMapping(value = "updateBuyDrugSwitch/{code}")
    public R<Boolean> updateBuyDrugSwitch(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                          @RequestBody UpdateBuyDrugSwitchDTO updateBuyDrugSwitchDTO) {
        BaseContextHandler.setTenant(code);
        RegGuideSaveDTO re = RegGuideSaveDTO.builder().build();
        RegGuide guide = guideApi.getGuide().getData();
        if (guide != null) {
            BeanUtil.copyProperties(guide, re);
        }
        re.setBuyDrugsUrlSwitch(updateBuyDrugSwitchDTO.getBuyDrugsUrlSwitch());
        re.setBuyDrugsUrl(updateBuyDrugSwitchDTO.getBuyDrugsUrl());
        guideApi.upsertByCode(re);
        return R.success();
    }


}
