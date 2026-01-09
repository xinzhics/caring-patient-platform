package com.caring.sass.nursing.controller.information;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.information.CompletionInformationStatisticsPageDTO;
import com.caring.sass.nursing.dto.information.CompletionInformationStatisticsSaveDTO;
import com.caring.sass.nursing.dto.information.CompletionInformationStatisticsUpdateDTO;
import com.caring.sass.nursing.entity.information.CompletionInformationStatistics;
import com.caring.sass.nursing.service.information.CompletionInformationStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 信息完整度统计
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/completionInformationStatistics")
@Api(value = "CompletionInformationStatistics", tags = "信息完整度统计")
//@PreAuth(replace = "completionInformationStatistics:")
public class CompletionInformationStatisticsController extends SuperController<CompletionInformationStatisticsService, Long, CompletionInformationStatistics, CompletionInformationStatisticsPageDTO, CompletionInformationStatisticsSaveDTO, CompletionInformationStatisticsUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<CompletionInformationStatistics> completionInformationStatisticsList = list.stream().map((map) -> {
            CompletionInformationStatistics completionInformationStatistics = CompletionInformationStatistics.builder().build();
            //TODO 请在这里完成转换
            return completionInformationStatistics;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(completionInformationStatisticsList));
    }

    @ApiOperation(value = "统计信息，通过时间和医助Id查询")
    @GetMapping
    public R<CompletionInformationStatistics> get(@RequestParam("date") LocalDate localDate,
                                                  @RequestParam("nursingId") Long nursingId) {
        CompletionInformationStatistics statistics = baseService.get(localDate, nursingId);
        return R.success(statistics);
    }

    @ApiOperation(value = "定时统计任务测试")
    @GetMapping("test/statistics")
    public R<String> get(@RequestParam("localDate") LocalDate localDate) {
        baseService.statisticsCompletenessInformationTask(localDate);
        return R.success("success");
    }

}
