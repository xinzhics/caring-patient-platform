package com.caring.sass.nursing.controller.drugs;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.entity.drugs.CustomDrugsCategory;
import com.caring.sass.nursing.dto.drugs.CustomDrugsCategorySaveDTO;
import com.caring.sass.nursing.dto.drugs.CustomDrugsCategoryUpdateDTO;
import com.caring.sass.nursing.dto.drugs.CustomDrugsCategoryPageDTO;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;
import com.caring.sass.nursing.service.drugs.CustomDrugsCategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.nursing.service.drugs.SysDrugsCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;

import javax.validation.constraints.NotEmpty;


/**
 * <p>
 * 前端控制器
 * 项目药品类别
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/customDrugsCategory")
@Api(value = "CustomDrugsCategory", tags = "项目药品类别")
//@PreAuth(replace = "customDrugsCategory:")
public class CustomDrugsCategoryController extends SuperController<CustomDrugsCategoryService, Long, CustomDrugsCategory, CustomDrugsCategoryPageDTO, CustomDrugsCategorySaveDTO, CustomDrugsCategoryUpdateDTO> {

    private final SysDrugsCategoryService sysDrugsCategoryService;

    public CustomDrugsCategoryController(SysDrugsCategoryService sysDrugsCategoryService) {
        this.sysDrugsCategoryService = sysDrugsCategoryService;
    }

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<CustomDrugsCategory> customDrugsCategoryList = list.stream().map((map) -> {
            CustomDrugsCategory customDrugsCategory = CustomDrugsCategory.builder().build();
            //TODO 请在这里完成转换
            return customDrugsCategory;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(customDrugsCategoryList));
    }

    /**
     * 修改项目推荐用药
     *
     * @param ids 新的推荐用药
     */
    @PutMapping("updateTenantCustomDrugsCategory")
    public R<Boolean> updateTenantCustomDrugsCategory(@RequestParam("ids[]") List<Long> ids) {
        // 删除项目原有用药分类配置
        String tenant = BaseContextHandler.getTenant();
        if (StrUtil.isBlank(tenant)) {
            return R.fail("项目编码不存在");
        }
        baseService.remove(Wraps.lbQ());

        // 新增新的用药分类配置
        if (CollUtil.isEmpty(ids)) {
            return R.success();
        }
        List<CustomDrugsCategory> customDrugsCategories = new ArrayList<>();
        for (Long id : ids) {
            SysDrugsCategory c = sysDrugsCategoryService.getById(id);
            if (c == null) {
                continue;
            }
            customDrugsCategories.add(CustomDrugsCategory.builder()
                    .categoryId(c.getId())
                    .build());
        }
        baseService.saveBatch(customDrugsCategories);
        return R.success();
    }


    @ApiOperation("复制项目药品类别")
    @PostMapping("copyDrugsCategory")
    public R<Boolean> copyDrugsCategory(@RequestParam("fromTenantCode") @NotEmpty(message = "待复制的项目编码不能为空") String fromTenantCode,
                              @RequestParam("toTenantCode") @NotEmpty(message = "目标项目编码不能为空") String toTenantCode) {
        baseService.copyDrugsCategory(fromTenantCode, toTenantCode);
        return R.success();
    }
}
