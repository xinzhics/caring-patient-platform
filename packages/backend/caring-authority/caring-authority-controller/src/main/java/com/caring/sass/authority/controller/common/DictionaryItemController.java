package com.caring.sass.authority.controller.common;


import cn.hutool.core.util.StrUtil;
import com.caring.sass.authority.dto.common.DictionaryItemSaveDTO;
import com.caring.sass.authority.dto.common.DictionaryItemUpdateDTO;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperCacheController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * 字典项
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dictionaryItem/anno")
@Api(value = "DictionaryItem", tags = "字典项")
//@PreAuth(replace = "dict:")
public class DictionaryItemController extends SuperCacheController<DictionaryItemService, Long, DictionaryItem, DictionaryItem, DictionaryItemSaveDTO, DictionaryItemUpdateDTO> {
    @Override
    public QueryWrap<DictionaryItem> handlerWrapper(DictionaryItem model, PageParams<DictionaryItem> params) {
        QueryWrap<DictionaryItem> wrapper = super.handlerWrapper(model, params);
        wrapper.lambda().ignore(DictionaryItem::setDictionaryType)
                .eq(DictionaryItem::getDictionaryType, model.getDictionaryType());
        return wrapper;
    }

    @ApiOperation("根据租户查询项目的字典")
    @GetMapping("queryTenantDict")
    public R<List<DictionaryItem>> queryTenantDict(@RequestParam(required = false, name = "tenantCode") String tenantCode) {
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        List<DictionaryItem> itemList = baseService.list(Wraps.<DictionaryItem>lbQ().orderByAsc(DictionaryItem::getSortValue));
        return R.success(itemList);
    }


    @Override
    public R<DictionaryItem> update(DictionaryItemUpdateDTO dictionaryItemUpdateDTO) {
        String tenantCode = dictionaryItemUpdateDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        return super.update(dictionaryItemUpdateDTO);
    }
}
