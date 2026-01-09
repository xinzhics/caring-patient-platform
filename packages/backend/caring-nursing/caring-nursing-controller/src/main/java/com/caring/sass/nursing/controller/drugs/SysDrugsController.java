package com.caring.sass.nursing.controller.drugs;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.drugs.SysDrugsPageDTO;
import com.caring.sass.nursing.dto.drugs.SysDrugsSaveDTO;
import com.caring.sass.nursing.dto.drugs.SysDrugsUpdateDTO;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategoryLink;
import com.caring.sass.nursing.entity.drugs.SysDrugsImage;
import com.caring.sass.nursing.service.drugs.*;
import com.caring.sass.nursing.util.DrugsImport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 药品
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/sysDrugs")
@Api(value = "SysDrugs", tags = "药品")
//@PreAuth(replace = "sysDrugs:")
public class SysDrugsController extends SuperController<SysDrugsService, Long, SysDrugs, SysDrugsPageDTO, SysDrugsSaveDTO, SysDrugsUpdateDTO> {

    private final SysDrugsCategoryService sysDrugsCategoryService;

    private final CustomDrugsCategoryService customDrugsCategoryService;

    private final SysDrugsCategoryLinkService sysDrugsCategoryLinkService;

    private final SysDrugsImageService sysDrugsImageService;

    private final CustomCommendDrugsService customCommendDrugsService;


    public SysDrugsController(SysDrugsCategoryService sysDrugsCategoryService,
                              SysDrugsImageService sysDrugsImageService, CustomDrugsCategoryService customDrugsCategoryService,
                              SysDrugsCategoryLinkService sysDrugsCategoryLinkService,
                              CustomCommendDrugsService customCommendDrugsService) {
        this.sysDrugsCategoryService = sysDrugsCategoryService;
        this.customDrugsCategoryService = customDrugsCategoryService;
        this.sysDrugsCategoryLinkService = sysDrugsCategoryLinkService;
        this.sysDrugsImageService = sysDrugsImageService;
        this.customCommendDrugsService = customCommendDrugsService;

    }

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<SysDrugs> sysDrugsList = list.stream().map((map) -> {
            SysDrugs sysDrugs = SysDrugs.builder().build();
            //TODO 请在这里完成转换
            return sysDrugs;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(sysDrugsList));
    }

    @Override
    public R<SysDrugs> save(SysDrugsSaveDTO sysDrugsSaveDTO) {

        List<SysDrugsCategoryLink> categoryLinkList = sysDrugsSaveDTO.getSysDrugsCategoryLinkList();
        List<SysDrugsImage> sysDrugsImages = sysDrugsSaveDTO.getSysDrugsImages();
        R<SysDrugs> sysDrugsR = super.save(sysDrugsSaveDTO);
        SysDrugs drugsRData = sysDrugsR.getData();
        sysDrugsCategoryLinkService.save(categoryLinkList, drugsRData.getId());
        sysDrugsImageService.save(sysDrugsImages, drugsRData.getId());
        return sysDrugsR;
    }

    @Override
    public R<SysDrugs> update(SysDrugsUpdateDTO sysDrugsUpdateDTO) {
        List<SysDrugsCategoryLink> categoryLinkList = sysDrugsUpdateDTO.getSysDrugsCategoryLinkList();
        List<SysDrugsImage> sysDrugsImages = sysDrugsUpdateDTO.getSysDrugsImages();
        R<SysDrugs> sysDrugsR = super.update(sysDrugsUpdateDTO);
        sysDrugsCategoryLinkService.save(categoryLinkList, sysDrugsUpdateDTO.getId());
        sysDrugsImageService.save(sysDrugsImages, sysDrugsUpdateDTO.getId());
        return sysDrugsR;
    }

    @Override
    public R<IPage<SysDrugs>> page(PageParams<SysDrugsPageDTO> params) {
        IPage<SysDrugs> page = params.buildPage();
        SysDrugsPageDTO queryModel = params.getModel();
        LbqWrapper<SysDrugs> lbqWrapper = Wraps.<SysDrugs>lbQ()
                .like(SysDrugs::getName, queryModel.getName())
                .like(SysDrugs::getManufactor, queryModel.getManufactor())
                .orderByDesc(SysDrugs::getSticky, SysDrugs::getStickyTime);

        Long categoryId = queryModel.getCategoryId();
        if (categoryId != null) {
            List<Long> childIds = sysDrugsCategoryService.childIds(categoryId);
            childIds.add(categoryId);
            List<SysDrugsCategoryLink> categoryLinks = sysDrugsCategoryLinkService.selectDrugsIdByCategoryIds(childIds);
            if (CollectionUtils.isEmpty(categoryLinks)) {
                return R.success(page);
            }
            List<Long> drugsIds = categoryLinks.stream().map(SysDrugsCategoryLink::getDrugsId).collect(Collectors.toList());
            lbqWrapper.in(SysDrugs::getId, drugsIds);
        }
        lbqWrapper.select(SysDrugs::getId, SysDrugs::getName, SysDrugs::getGenericName, SysDrugs::getIcon, SysDrugs::getGyzz,
                SysDrugs::getManufactor, SysDrugs::getSpec, SysDrugs::getUsage, SysDrugs::getIsOtc, SysDrugs::getSticky);
        baseService.page(page, lbqWrapper);

        List<SysDrugs> records = page.getRecords();

        sysDrugsCategoryLinkService.getCategoryLink(records);
        return R.success(page);
    }

    @Override
    public R<SysDrugs> get(@PathVariable Long id) {

        R<SysDrugs> drugsR = super.get(id);
        if (drugsR.getIsSuccess()) {
            SysDrugs drugsRData = drugsR.getData();
            List<SysDrugs> list = new ArrayList<>();
            if (Objects.nonNull(drugsRData)) {
                list.add(drugsRData);
                sysDrugsCategoryLinkService.getCategoryLink(list);
                LbqWrapper<SysDrugsImage> imageLbqWrapper = new LbqWrapper<>();
                imageLbqWrapper.eq(SysDrugsImage::getDrugsId, id);
                List<SysDrugsImage> drugsImages = sysDrugsImageService.list(imageLbqWrapper);
                drugsRData.setSysDrugsImages(drugsImages);
            }
        }
        return drugsR;
    }

    @ApiOperation(value = "项目用药分页列表查询")
    @PostMapping(value = "/pageByTenant/{tenant}")
    public R<IPage<SysDrugs>> pageByTenant(@RequestBody @Validated PageParams<SysDrugsPageDTO> params,
                                           @PathVariable(value = "tenant") @NotEmpty(message = "项目编码不能为空") String tenant) {
        BaseContextHandler.setTenant(tenant);
        IPage<SysDrugs> page = params.buildPage();

        // 获取项目 勾选的药品分类
        List<Long> categoryIds = customDrugsCategoryService.selectCategoryIds();
        SysDrugsPageDTO queryModel = params.getModel();
        LbqWrapper<SysDrugs> lbqWrapper = Wraps.<SysDrugs>lbQ().orderByDesc(SysDrugs::getSticky).orderByAsc(SysDrugs::getPyszm);
        String queryName = queryModel.getName();
        if (StringUtils.isNotEmptyString(queryName)) {
            lbqWrapper.nested(i -> i.like(SysDrugs::getName, queryName)
                    .or().like(SysDrugs::getGenericName, queryName));
        }
        if (StringUtils.isNotEmptyString(queryModel.getCode())) {
            lbqWrapper.eq(SysDrugs::getCode, queryModel.getCode());
        }
        if (CollUtil.isNotEmpty(categoryIds)) {

            // 根据分类查找相关的药品ID集合
            List<SysDrugsCategoryLink> categoryLinks = sysDrugsCategoryLinkService.selectDrugsIdByCategoryIds(categoryIds);
            List<Long> drugsIds = categoryLinks.stream().map(SysDrugsCategoryLink::getDrugsId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(drugsIds)) {
                lbqWrapper.in(SysDrugs::getId, drugsIds);
            }
        }
        lbqWrapper.select(SysDrugs::getId, SysDrugs::getName, SysDrugs::getGenericName, SysDrugs::getIcon, SysDrugs::getGyzz,
                SysDrugs::getManufactor, SysDrugs::getSpec, SysDrugs::getUsage, SysDrugs::getIsOtc, SysDrugs::getSticky);
        baseService.page(page, lbqWrapper);
        return R.success(page);
    }

    /**
     * @Author yangShuai
     * @Description 删除药品前。删除药品的分类关联、删除推荐用药
     * @Date 2021/5/10 13:15
     *
     * @param longs
     * @return com.caring.sass.base.R<java.lang.Boolean>
     */
    @Override
    @Transactional
    public R<Boolean> handlerDelete(List<Long> longs) {
        sysDrugsCategoryLinkService.deleteByDrugsIds(longs);
        customCommendDrugsService.deleteByDrugsIds(longs);
        sysDrugsImageService.deleteByDrugsIds(longs);
        return R.success(true).setDefExec(true);
    }

    @Override
    public void query(PageParams<SysDrugsPageDTO> params, IPage<SysDrugs> page, Long defSize) {
        SysDrugsPageDTO queryModel = params.getModel();
        Long categoryId = queryModel.getCategoryId();
        if (categoryId != null) {
            LbqWrapper<SysDrugs> lbqWrapper = Wraps.<SysDrugs>lbQ();
            List<Long> childIds = sysDrugsCategoryService.childIds(categoryId);
            childIds.add(categoryId);
            lbqWrapper.in(SysDrugs::getCategoryId, childIds);
            this.getBaseService().page(page, lbqWrapper);
            this.handlerResult(page);
        } else {
            super.query(params, page, defSize);
        }

    }

    @ApiOperation(value = "修改药品拼音字段")
    @GetMapping("anno/tempController/initpyszm")
    public void importDrugsFromZip(Long fileId, String folderName, String fileName) {
        drugsImport.importDrugsFromZip(fileId, folderName, fileName);
    }

    @Autowired
    DrugsImport drugsImport;



}
