package com.caring.sass.nursing.controller.drugs;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.drugs.PatientDrugsHistoryLogPageDTO;
import com.caring.sass.nursing.dto.drugs.PatientDrugsHistoryLogSaveDTO;
import com.caring.sass.nursing.dto.drugs.PatientDrugsHistoryLogUpdateDTO;
import com.caring.sass.nursing.entity.drugs.PatientDrugsHistoryLog;
import com.caring.sass.nursing.service.drugs.PatientDrugsHistoryLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 患者的药箱
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patientDrugsHistoryLog")
@Api(value = "PatientDrugsHistoryLog", tags = "患者药箱变更记录")
public class PatientDrugsHistoryLogController extends SuperController<PatientDrugsHistoryLogService, Long, PatientDrugsHistoryLog, PatientDrugsHistoryLogPageDTO, PatientDrugsHistoryLogSaveDTO, PatientDrugsHistoryLogUpdateDTO> {


    @ApiOperation("用药历史记录页面 不分组， 返回null表示没有了")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码，从0开始", defaultValue="0", required = true),
            @ApiImplicitParam(name = "patientId", value = "患者id", defaultValue="0", required = true),
    })
    @PostMapping("app/historyDate/page")
    public R<List<PatientDrugsHistoryLog>> appHistoryDatePage(@RequestParam("page") int page,
                                                              @RequestParam("patientId") Long patientId) {

        LbqWrapper<PatientDrugsHistoryLog> wrapper = Wraps.<PatientDrugsHistoryLog>lbQ().eq(PatientDrugsHistoryLog::getPatientId, patientId)
                .select(PatientDrugsHistoryLog::getHistoryDate)
                .groupBy(PatientDrugsHistoryLog::getHistoryDate)
                .orderByDesc(PatientDrugsHistoryLog::getHistoryDate)
                .last(" limit " + page * 5 + ", 5");
        List<PatientDrugsHistoryLog> historyLogs = baseService.list(wrapper);
        if (CollUtil.isEmpty(historyLogs)) {
            return R.success(null);
        }
        List<LocalDate> localDateList = historyLogs.stream().map(PatientDrugsHistoryLog::getHistoryDate)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(localDateList)) {
            return R.success(null);
        }
        LbqWrapper<PatientDrugsHistoryLog> historyLogLbqWrapper = Wraps.<PatientDrugsHistoryLog>lbQ()
                .eq(PatientDrugsHistoryLog::getPatientId, patientId)
                .in(PatientDrugsHistoryLog::getHistoryDate, localDateList)
                .orderByDesc(PatientDrugsHistoryLog::getHistoryDate)
                .orderByDesc(PatientDrugsHistoryLog::getOperateTypeSort);
        List<PatientDrugsHistoryLog> drugsHistoryLogs = baseService.list(historyLogLbqWrapper);
        if (CollUtil.isEmpty(drugsHistoryLogs)) {
            return R.success(null);
        }
        return R.success(drugsHistoryLogs);
    }



    @ApiOperation("用药历史记录页面， 返回null表示没有了")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码，从0开始", defaultValue="0", required = true),
            @ApiImplicitParam(name = "patientId", value = "患者id", defaultValue="0", required = true),
    })
    @PostMapping("historyDate/page")
    public R<Map<LocalDate, List<PatientDrugsHistoryLog>>> historyDatePage(@RequestParam("page") int page,
                                                                           @RequestParam("patientId") Long patientId) {

        LbqWrapper<PatientDrugsHistoryLog> wrapper = Wraps.<PatientDrugsHistoryLog>lbQ().eq(PatientDrugsHistoryLog::getPatientId, patientId)
                .select(PatientDrugsHistoryLog::getHistoryDate)
                .groupBy(PatientDrugsHistoryLog::getHistoryDate)
                .orderByDesc(PatientDrugsHistoryLog::getHistoryDate)
                .last(" limit " + page * 5 + ", 5");
        List<PatientDrugsHistoryLog> historyLogs = baseService.list(wrapper);
        if (CollUtil.isEmpty(historyLogs)) {
            return R.success(null);
        }
        List<LocalDate> localDateList = historyLogs.stream().map(PatientDrugsHistoryLog::getHistoryDate)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(localDateList)) {
            return R.success(null);
        }
        LbqWrapper<PatientDrugsHistoryLog> historyLogLbqWrapper = Wraps.<PatientDrugsHistoryLog>lbQ()
                .eq(PatientDrugsHistoryLog::getPatientId, patientId)
                .in(PatientDrugsHistoryLog::getHistoryDate, localDateList)
                .orderByDesc(PatientDrugsHistoryLog::getHistoryDate)
                .orderByDesc(PatientDrugsHistoryLog::getOperateTypeSort);
        List<PatientDrugsHistoryLog> drugsHistoryLogs = baseService.list(historyLogLbqWrapper);
        if (CollUtil.isEmpty(drugsHistoryLogs)) {
            return R.success(null);
        }
        Map<LocalDate, List<PatientDrugsHistoryLog>> groupByHistoryDate = drugsHistoryLogs.stream()
                .collect(Collectors.groupingBy(PatientDrugsHistoryLog::getHistoryDate));
        return R.success(groupByHistoryDate);
    }


}
