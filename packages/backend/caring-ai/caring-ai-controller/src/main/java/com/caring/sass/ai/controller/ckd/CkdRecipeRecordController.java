package com.caring.sass.ai.controller.ckd;

import com.caring.sass.ai.entity.CkdRecipeRecord;
import com.caring.sass.ai.dto.CkdRecipeRecordSaveDTO;
import com.caring.sass.ai.dto.CkdRecipeRecordUpdateDTO;
import com.caring.sass.ai.dto.CkdRecipeRecordPageDTO;
import com.caring.sass.ai.service.CkdRecipeRecordService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 用户食谱记录
 * </p>
 *
 * @author leizhi
 * @date 2025-04-23
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ckdRecipeRecord")
@Api(value = "CkdRecipeRecord", tags = "用户食谱记录")
public class CkdRecipeRecordController extends SuperController<CkdRecipeRecordService, Long, CkdRecipeRecord, CkdRecipeRecordPageDTO, CkdRecipeRecordSaveDTO, CkdRecipeRecordUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<CkdRecipeRecord> ckdRecipeRecordList = list.stream().map((map) -> {
            CkdRecipeRecord ckdRecipeRecord = CkdRecipeRecord.builder().build();
            //TODO 请在这里完成转换
            return ckdRecipeRecord;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(ckdRecipeRecordList));
    }
}
