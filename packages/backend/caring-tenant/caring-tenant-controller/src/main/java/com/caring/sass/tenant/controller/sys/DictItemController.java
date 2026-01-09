package com.caring.sass.tenant.controller.sys;

import com.caring.sass.tenant.dto.sys.DictItemPageDTO;
import com.caring.sass.tenant.dto.sys.DictItemSaveDTO;
import com.caring.sass.tenant.dto.sys.DictItemUpdateDTO;
import com.caring.sass.tenant.entity.sys.DictItem;
import com.caring.sass.tenant.service.sys.DictItemService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 字典项
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dictItem")
@Api(value = "DictItem", tags = "字典项")
//@PreAuth(replace = "dictItem:")
public class DictItemController extends SuperController<DictItemService, Long, DictItem, DictItemPageDTO, DictItemSaveDTO, DictItemUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<DictItem> dictItemList = list.stream().map((map) -> {
            DictItem dictItem = DictItem.builder().build();
            //TODO 请在这里完成转换
            return dictItem;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(dictItemList));
    }
}
