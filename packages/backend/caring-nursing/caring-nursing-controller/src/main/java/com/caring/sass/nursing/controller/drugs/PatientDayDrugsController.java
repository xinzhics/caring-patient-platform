package com.caring.sass.nursing.controller.drugs;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.drugs.CalendarVo;
import com.caring.sass.nursing.dto.drugs.PatientDayDrugsPageDTO;
import com.caring.sass.nursing.dto.drugs.PatientDayDrugsSaveDTO;
import com.caring.sass.nursing.dto.drugs.PatientDayDrugsUpdateDTO;
import com.caring.sass.nursing.entity.drugs.PatientDayDrugs;
import com.caring.sass.nursing.service.drugs.PatientDayDrugsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalTime;


/**
 * <p>
 * 前端控制器
 * 患者每天的用药量记录（一天生成一次）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patientDayDrugs")
@Api(value = "PatientDayDrugs", tags = "患者每天的用药量记录（一天生成一次）")
public class PatientDayDrugsController extends SuperController<PatientDayDrugsService, Long, PatientDayDrugs, PatientDayDrugsPageDTO, PatientDayDrugsSaveDTO, PatientDayDrugsUpdateDTO> {


    @ApiOperation("患者用药日历")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "月份，格式如：2020-11", dataType = "string", paramType = "query")
    })
    @GetMapping({"/calendar/{patientId}"})
    public R<CalendarVo> findMedicationCalendarInfo(@PathVariable("patientId") Long patientId,
                                                    @RequestParam("date") @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "日期格式错误") String date) {
        CalendarVo calendarVo = baseService.calendar(date, patientId);
        return R.success(calendarVo);
    }

    @ApiOperation("患者7日数据")
    @GetMapping({"/7Day/{patientId}"})
    public R<CalendarVo> findMedicationCalendarInfo(@PathVariable("patientId") Long patientId) {
        LocalDate now = LocalDate.now();
        LocalDate startTime = now.plusDays(-6);
        CalendarVo calendarVo = baseService.calendar(startTime, now, patientId);
        return R.success(calendarVo);
    }




}
