package com.caring.sass.nursing.controller.form;


import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.dto.form.*;
import com.caring.sass.nursing.entity.form.IndicatorsResultInformation;
import com.caring.sass.nursing.service.form.IndicatorsResultInformationService;
import com.caring.sass.nursing.vo.*;
import com.caring.sass.security.annotation.PreAuth;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * 患者监测数据结果及处理表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/indicatorsResultInformation")
@Api(value = "IndicatorsResultInformation", tags = "患者监测数据结果及处理表")
@PreAuth(replace = "indicatorsResultInformation:")
public class IndicatorsResultInformationController extends SuperController<IndicatorsResultInformationService, Long, IndicatorsResultInformation, IndicatorsResultInformationPageDTO, IndicatorsResultInformationSaveDTO, IndicatorsResultInformationUpdateDTO> {

    @ApiOperationSupport(author = "李祥", order = 1)
    @ApiOperation(value = "监测数据的异常患者数")
    @GetMapping("MonitorUnusualPatients")
    public R<Integer> MonitorUnusualPatients() {
        return R.success(baseService.monitorUnusualPatients(BaseContextHandler.getTenant()));
    }

    @ApiOperationSupport(author = "李祥", order =2)
    @ApiOperation(value = "监测数据计划列表（包含未处理异常患者数量）")
    @GetMapping("abnormalData")
    public R<List<AbnormalDataVo>> abnormalData() {
        return R.success(baseService.abnormalData(BaseContextHandler.getTenant()));
    }

    @ApiOperationSupport(author = "李祥", order =3)
    @ApiOperation(value = "监测数据-异常数据未处理列表")
    @PostMapping("dataUnprocessedList")
    public R<MonitorUnprocessedAndTotal> dataUnprocessedList(@RequestBody @Valid PageParams<MonitorUnprocessedDTO> params) {

        return R.success(baseService.dataUnprocessedList(params));
    }

    @ApiOperationSupport(author = "李祥", order =4)
    @ApiOperation(value = "监测数据-异常数据已处理列表")
    @PostMapping("dataProcessedList")
    public R<MonitorProcessedAndTotal> dataProcessedList(@RequestBody @Valid PageParams<MonitorUnprocessedDTO> params) {

        return R.success(baseService.dataProcessedList(params));
    }

    @ApiOperationSupport(author = "李祥", order =5)
    @ApiOperation(value = "异常处理（单个或全部处理）")
    @PostMapping("UnprocessedEliminate")
    public R UnprocessedEliminate(@RequestBody @Valid EliminateDto dto) {
        return R.success(baseService.UnprocessedEliminate(dto));
    }

    @ApiOperationSupport(author = "李祥", order =6)
    @ApiOperation(value = "已处理异常数据清除标记")
    @GetMapping("ProcessedEliminate/{planId}")
    public R ProcessedEliminate(@PathVariable("planId") Long planId) {
        baseService.ProcessedEliminate(planId);
        return R.success();
    }

    @ApiOperationSupport(author = "李祥", order =7)
    @ApiOperation(value = "统计异常数量")
    @GetMapping("StatisticsAbnormalQuantity/{date}/{planId}")
    public R<SubmittedQuantity> StatisticsAbnormalQuantity(@PathVariable("date") String date,@PathVariable("planId") Long planId) {
        return R.success(baseService.statisticsAbnormalQuantity(date,planId));
    }

    @ApiOperationSupport(author = "李祥", order =8)
    @ApiOperation(value = "监测计划说明")
    @GetMapping("explain/{planId}")
    public R<List<ExplainVo>> explain(@PathVariable("planId") Long planId) {
        return R.success(baseService.explain(planId));
    }

}