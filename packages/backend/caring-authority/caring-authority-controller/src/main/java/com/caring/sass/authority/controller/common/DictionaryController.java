package com.caring.sass.authority.controller.common;


import com.caring.sass.authority.dto.common.DictionarySaveDTO;
import com.caring.sass.authority.dto.common.DictionaryUpdateDTO;
import com.caring.sass.authority.entity.common.Dictionary;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.authority.service.common.DictionaryService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * 字典类型
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
@Slf4j
@Validated
@Deprecated
@RestController
@RequestMapping("/dictionary")
@Api(value = "Dictionary", tags = "字典类型")
@PreAuth(replace = "dict:")
public class DictionaryController extends SuperController<DictionaryService, Long, Dictionary, Dictionary, DictionarySaveDTO, DictionaryUpdateDTO> {

    @Autowired
    private DictionaryItemService dictionaryItemService;

    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        this.baseService.removeByIds(ids);
        this.dictionaryItemService.remove(Wraps.<DictionaryItem>lbQ().in(DictionaryItem::getDictionaryId, ids));
        return this.success(true);
    }
}
