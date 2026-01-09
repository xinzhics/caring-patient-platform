package com.caring.sass.tenant.controller.sys;

import com.caring.sass.tenant.dto.sys.DictPageDTO;
import com.caring.sass.tenant.dto.sys.DictSaveDTO;
import com.caring.sass.tenant.dto.sys.DictUpdateDTO;
import com.caring.sass.tenant.entity.sys.Dict;
import com.caring.sass.tenant.service.sys.DictService;
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
 * 系统字典类型
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dict")
@Api(value = "Dict", tags = "系统字典类型")
//@PreAuth(replace = "dict:")
public class DictController extends SuperController<DictService, Long, Dict, DictPageDTO, DictSaveDTO, DictUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<Dict> dictList = list.stream().map((map) -> {
            Dict dict = Dict.builder().build();
            //TODO 请在这里完成转换
            return dict;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(dictList));
    }
}
