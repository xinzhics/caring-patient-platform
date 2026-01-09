package com.caring.sass.nursing.controller.drugs;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;
import com.caring.sass.nursing.dto.drugs.SysDrugsCategorySaveDTO;
import com.caring.sass.nursing.dto.drugs.SysDrugsCategoryUpdateDTO;
import com.caring.sass.nursing.dto.drugs.SysDrugsCategoryPageDTO;
import com.caring.sass.nursing.service.drugs.SysDrugsCategoryService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.utils.TreeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 药品类别
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/sysDrugsCategory")
@Api(value = "SysDrugsCategory", tags = "药品类别")
//@PreAuth(replace = "sysDrugsCategory:")
public class SysDrugsCategoryController extends SuperController<SysDrugsCategoryService, Long, SysDrugsCategory, SysDrugsCategoryPageDTO, SysDrugsCategorySaveDTO, SysDrugsCategoryUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<SysDrugsCategory> sysDrugsCategoryList = list.stream().map((map) -> {
            SysDrugsCategory sysDrugsCategory = SysDrugsCategory.builder().build();
            //TODO 请在这里完成转换
            return sysDrugsCategory;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(sysDrugsCategoryList));
    }


    /**
     * 查询系统药品分类树
     *
     * @param parentId 父ID
     */
    @ApiOperation(value = "查询系统药品分类树", notes = "查询系统药品分类树")
    @GetMapping("/tree")
    @SysLog("查询系统药品分类树")
    public R<List<SysDrugsCategory>> tree(@RequestParam(required = false) Long parentId) {
        // todo 缓存
        List<SysDrugsCategory> list = this.baseService.list(Wraps.<SysDrugsCategory>lbQ()
                .eq(SysDrugsCategory::getParentId, parentId)
                .orderByDesc(SysDrugsCategory::getId));
        return this.success(TreeUtil.buildTree(list));
    }


    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        // 删除父类的同时，需要删除子类
        baseService.recursively(ids);
        return R.success();
    }
}
