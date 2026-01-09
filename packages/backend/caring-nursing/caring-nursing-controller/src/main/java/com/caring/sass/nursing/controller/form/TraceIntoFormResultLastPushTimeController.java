package com.caring.sass.nursing.controller.form;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.traceInto.*;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFormResultLastPushTime;
import com.caring.sass.nursing.service.traceInto.TraceIntoFormResultLastPushTimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;

import javax.validation.constraints.NotNull;


/**
 * <p>
 * 前端控制器
 * 选项跟踪患者最新上传时间记录表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/traceIntoFormResultLastPushTime")
@Api(value = "TraceIntoFormResultLastPushTime", tags = "选项跟踪患者最新上传时间记录表")
public class TraceIntoFormResultLastPushTimeController extends SuperController<TraceIntoFormResultLastPushTimeService, Long, TraceIntoFormResultLastPushTime, TraceIntoFormResultLastPushTimePageDTO, TraceIntoFormResultLastPushTimeSaveDTO, TraceIntoFormResultLastPushTimeUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<TraceIntoFormResultLastPushTime> traceIntoFormResultLastPushTimeList = list.stream().map((map) -> {
            TraceIntoFormResultLastPushTime traceIntoFormResultLastPushTime = TraceIntoFormResultLastPushTime.builder().build();
            //TODO 请在这里完成转换
            return traceIntoFormResultLastPushTime;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(traceIntoFormResultLastPushTimeList));
    }


    @GetMapping("getAppTracePlanList")
    @ApiOperation("获取异常选项跟踪随访计划列表")
    public R<List<AppTracePlanList>> getAppTracePlanList(@RequestParam("nursingId") Long nursingId) {

        List<AppTracePlanList> appTracePlanLists = baseService.getAppTracePlanList(nursingId);
        return R.success(appTracePlanLists);

    }


    @GetMapping("countHandleNumber")
    @ApiOperation("统计未处理已处理数量")
    public R<AppTracePlanList> countHandleNumber(@RequestParam("nursingId") Long nursingId, Long planId) {

        AppTracePlanList appTracePlanList = baseService.countHandleNumber(nursingId, planId);
        return R.success(appTracePlanList);

    }



    @PostMapping("appPage")
    @ApiOperation("app未处理已处理数据列表")
    public R<IPage<TraceFormPatientResultPageDTO>> appPage(@RequestBody @Validated PageParams<TraceIntoFormResultLastPushTimePageDTO> pageParams) {

        IPage<TraceIntoFormResultLastPushTime> page = pageParams.buildPage();
        TraceIntoFormResultLastPushTimePageDTO model = pageParams.getModel();
        LbqWrapper<TraceIntoFormResultLastPushTime> lbqWrapper = Wraps.<TraceIntoFormResultLastPushTime>lbQ();
        lbqWrapper.eq(TraceIntoFormResultLastPushTime::getPlanId, model.getPlanId());
        lbqWrapper.eq(TraceIntoFormResultLastPushTime::getHandleStatus, model.getHandleStatus());
        Long nursingId = model.getNursingId();
        String patientName = model.getPatientName();
        lbqWrapper.eq(TraceIntoFormResultLastPushTime::getNursingId, nursingId);
        if (StrUtil.isNotBlank(patientName)) {
            String tenant = BaseContextHandler.getTenant();
            lbqWrapper.apply(true, " patient_id in (select id from u_user_patient where tenant_code = '"+tenant+"' and service_advisor_id = "+nursingId+" and name like '%"+patientName+"%') ");
        }
        IPage<TraceFormPatientResultPageDTO> pageDTOIPage = baseService.appPage(page, lbqWrapper);
        return R.success(pageDTOIPage);
    }



    @GetMapping("handleOnePatientResult")
    @ApiOperation("app处理某患者的异常数据")
    public R<Boolean> handleResult(@RequestParam Long patientId, @RequestParam Long formId) {
        baseService.handleResult(patientId, formId);
        return R.success(true);
    }


    @GetMapping("allHandleResult")
    @ApiOperation("app处理全部患者异常数据")
    public R<Boolean> allHandleResult(@RequestParam Long nursingId, @RequestParam Long formId) {
        baseService.allHandleResult(nursingId, formId);
        return R.success(true);
    }

    @GetMapping("allClearData")
    @ApiOperation("全部清空数据")
    public R<Boolean> allClearData(@RequestParam Long nursingId, @RequestParam Long formId) {
        baseService.allClearData(nursingId, formId);
        return R.success(true);
    }


}
