package com.caring.sass.nursing.controller.form;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.nursing.dto.traceInto.*;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResult;
import com.caring.sass.nursing.service.traceInto.TraceIntoResultService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 选项跟踪结果异常表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/traceIntoResult")
@Api(value = "TraceIntoResult", tags = "选项跟踪结果异常表")
public class TraceIntoResultController extends SuperController<TraceIntoResultService, Long, TraceIntoResult, TraceIntoResultPageDTO, TraceIntoResultSaveDTO, TraceIntoResultUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<TraceIntoResult> traceIntoResultList = list.stream().map((map) -> {
            TraceIntoResult traceIntoResult = TraceIntoResult.builder().build();
            //TODO 请在这里完成转换
            return traceIntoResult;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(traceIntoResultList));
    }
}
