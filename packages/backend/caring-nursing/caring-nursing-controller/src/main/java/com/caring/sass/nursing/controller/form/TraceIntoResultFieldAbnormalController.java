package com.caring.sass.nursing.controller.form;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.nursing.dto.traceInto.*;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResultFieldAbnormal;
import com.caring.sass.nursing.service.traceInto.TraceIntoResultFieldAbnormalService;
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
 * 选项跟踪异常题目明细记录表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/traceIntoResultFieldAbnormal")
@Api(value = "TraceIntoResultFieldAbnormal", tags = "选项跟踪异常题目明细记录表")
public class TraceIntoResultFieldAbnormalController extends SuperController<TraceIntoResultFieldAbnormalService, Long, TraceIntoResultFieldAbnormal, TraceIntoResultFieldAbnormalPageDTO, TraceIntoResultFieldAbnormalSaveDTO, TraceIntoResultFieldAbnormalUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<TraceIntoResultFieldAbnormal> traceIntoResultFieldAbnormalList = list.stream().map((map) -> {
            TraceIntoResultFieldAbnormal traceIntoResultFieldAbnormal = TraceIntoResultFieldAbnormal.builder().build();
            //TODO 请在这里完成转换
            return traceIntoResultFieldAbnormal;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(traceIntoResultFieldAbnormalList));
    }

    @ApiOperation("统计")
    @GetMapping("statistics")
    public R<TraceIntoOptionStatisticsDTO> statistics(@RequestParam LocalDate localDate,
                                                      @RequestParam Long nursingId,
                                                      @RequestParam Long formId) {


        TraceIntoOptionStatisticsDTO statisticsDTO = baseService.statistics(localDate, nursingId, formId);
        return R.success(statisticsDTO);
    }

}
