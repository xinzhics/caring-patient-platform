package com.caring.sass.nursing.controller.form;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.constant.MonitorQueryDateDTO;
import com.caring.sass.nursing.dto.blood.SugarDTO;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.SugarFormResult;
import com.caring.sass.nursing.dto.form.SugarFormResultSaveDTO;
import com.caring.sass.nursing.dto.form.SugarFormResultUpdateDTO;
import com.caring.sass.nursing.dto.form.SugarFormResultPageDTO;
import com.caring.sass.nursing.service.form.SugarFormResultService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.nursing.service.task.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 血糖表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/sugarFormResult")
@Api(value = "SugarFormResult", tags = "血糖表")
public class SugarFormResultController extends SuperController<SugarFormResultService, String, SugarFormResult, SugarFormResultPageDTO, SugarFormResultSaveDTO, SugarFormResultUpdateDTO> {

    @ApiOperation("血糖走势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型(0凌晨 1早餐前 2早餐后 3午餐前 4午餐后 5晚餐前 6晚餐后 7睡前)", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "week", value = "周", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "patientId", value = "患者id", dataType = "long", paramType = "path")
    })
    @GetMapping("loadMyBloodSugarTrendData/{patientId}")
    public R<List<Map<String, Object>>> loadMyBloodSugarTrendData(@RequestParam("type") Integer type,
                                                                  @RequestParam("week") Integer week,
                                                                  @PathVariable("patientId") Long patientId) {
        List<Map<String, Object>> result = baseService.loadPatientBloodSugarTrendData(type, week, patientId);
        return R.success(result);
    }

    @ApiOperation("血糖记录")
    @GetMapping("findSugarByTime/{patientId}")
    public R<List<SugarDTO>> findSugarByTime(@PathVariable("patientId") Long patientId,
                                             Long startTime,
                                             Long endTime) {
        if (startTime != null && endTime != null) {
            startTime = DateUtils.startDayTime(new Date(startTime)).getTime();
            endTime = DateUtils.lastDateForDay(new Date(endTime)).getTime();
        }
        List<SugarDTO> list = baseService.findSugarByTime(Convert.toStr(patientId), startTime, endTime);
        return R.success(list);
    }


    @ApiOperation("分页查询血糖记录")
    @PostMapping("findSugarByTime/monitorQueryDate")
    public R<IPage<SugarFormResult>> findSugarByTime(@RequestBody @Validated PageParams<SugarFormResultPageDTO> params) {

        SugarFormResultPageDTO model = params.getModel();
        IPage<SugarFormResult> buildPage = params.buildPage();
        MonitorQueryDateDTO monitorQueryDate = model.getMonitorQueryDate();
        if (monitorQueryDate == null) {
            monitorQueryDate = MonitorQueryDateDTO.CURRENT_DAY;
        }
        LocalDateTime minTime = MonitorQueryDateDTO.getMinTime(monitorQueryDate);
        LocalDateTime maxTime = MonitorQueryDateDTO.getMaxTime(monitorQueryDate);
        LbqWrapper<SugarFormResult> wrapper = Wraps.<SugarFormResult>lbQ()
                .eq(SugarFormResult::getPatientId, model.getPatientId())
                .orderByDesc(SuperEntity::getCreateTime);
        if (minTime != null) {
            long startTime = minTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            wrapper.gt(SugarFormResult::getCreateDay, startTime / 1000L);
        }
        if (maxTime != null) {
            long endTime = maxTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            wrapper.lt(SugarFormResult::getCreateDay, endTime / 1000L);
        }
        baseService.page(buildPage, wrapper);
        return R.success(buildPage);
    }

    @ApiOperation("一周血糖填写详情")
    @GetMapping("list/{patientId}")
    public R<?> list(@RequestParam("week") Integer week,
                     @PathVariable("patientId") Long patientId) {
        JSONObject jsonObject = baseService.list(week, patientId);
        return R.success(jsonObject);
    }



    @ApiOperation("通过消息ID查询数据")
    @GetMapping("getByMessageId")
    public R<SugarFormResult> getByMessageId(@RequestParam("messageId") Long messageId) {
        SugarFormResult sugarFormResult = baseService.getOne(Wraps.<SugarFormResult>lbQ()
                .eq(SugarFormResult::getMessageId, messageId));
        return R.success(sugarFormResult);
    }

    @Override
    public R<SugarFormResult> handlerUpdate(SugarFormResultUpdateDTO model) {
        SugarFormResult sugarFormResult = BeanUtil.toBean(model, SugarFormResult.class);
        Long createDay = sugarFormResult.getCreateDay();
        if (Objects.nonNull(createDay)) {
            boolean isTimeMillis = NumberUtil.toStr(createDay).length() == 13;
            if (isTimeMillis) {
                sugarFormResult.setCreateDay(createDay / 1000L);
            }
        }
        baseService.updateById(sugarFormResult);
        return R.success(sugarFormResult);
    }
}
