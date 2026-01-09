package com.caring.sass.nursing.controller.drugs;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import com.caring.sass.nursing.dto.drugs.PatientDrugsPageDTO;
import com.caring.sass.nursing.dto.drugs.PatientDrugsSaveDTO;
import com.caring.sass.nursing.dto.drugs.PatientDrugsUpdateDTO;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTimeSetting;
import com.caring.sass.nursing.service.drugs.PatientDrugsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 患者添加的用药
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patientDrugs")
@Api(value = "PatientDrugs", tags = "患者添加的用药")
//@PreAuth(replace = "patientDrugs:")
public class PatientDrugsController extends SuperController<PatientDrugsService, Long, PatientDrugs, PatientDrugsPageDTO, PatientDrugsSaveDTO, PatientDrugsUpdateDTO> {

    @Autowired
    ImApi imApi;

    @ApiOperation("查询患者的正在用药和历史用药")
    @GetMapping("patientDrugsListAndHistory/{patientId}")
    public R<Map<String, List<PatientDrugs>>> patientDrugsListAndHistory(@PathVariable("patientId") Long patientId) {
        // 当前用药
        LbqWrapper<PatientDrugs> wrapper = Wraps.<PatientDrugs>lbQ()
                .eq(PatientDrugs::getStatus, 0)
                .eq(PatientDrugs::getPatientId, patientId);
        List<PatientDrugs> drugsList = baseService.list(wrapper);
        setDrugsTimeSetting(drugsList);
        // 历史用药
        LbqWrapper<PatientDrugs> historyWrapper = Wraps.<PatientDrugs>lbQ()
                .ne(PatientDrugs::getStatus, 0)
                .eq(PatientDrugs::getPatientId, patientId);
        List<PatientDrugs> historyMedicines = baseService.list(historyWrapper);
        setDrugsTimeSetting(historyMedicines);

        Map<String, List<PatientDrugs>> result = new HashMap<>();
        result.put("medicines", drugsList);
        result.put("historyMedicines", historyMedicines);
        return R.success(result);
    }

    @ApiOperation("患者的正在用药")
    @GetMapping("patientDrugsList/{patientId}")
    public R<List<PatientDrugs>> patientDrugsList(@PathVariable("patientId") Long patientId) {

        // 当前用药
        LbqWrapper<PatientDrugs> wrapper = Wraps.<PatientDrugs>lbQ()
                .eq(PatientDrugs::getCycleDuration, 1)
                .eq(PatientDrugs::getTimePeriod, PatientDrugsTimePeriodEnum.day)
                .eq(PatientDrugs::getStatus, 0)
                .eq(PatientDrugs::getPatientId, patientId);
        List<PatientDrugs> drugsList = baseService.list(wrapper);
        setDrugsTimeSetting(drugsList);
        return R.success(drugsList);

    }

    public void setDrugsTimeSetting(List<PatientDrugs> drugsList) {

        if (CollUtil.isNotEmpty(drugsList)) {
            baseService.setDrugsTimeSetting(drugsList);
        }

    }


    @ApiOperation("修改提醒使用 修改患者用药时间")
    @PutMapping("patientDrugsList")
    public R<List<PatientDrugs>> patientDrugsList(@RequestBody List<PatientDrugs> patientDrugs) {

        // 当前用药
        if (CollUtil.isNotEmpty(patientDrugs)) {
            for (PatientDrugs patientDrug : patientDrugs) {
                List<PatientDrugsTimeSetting> timeSettingList = patientDrug.getPatientDrugsTimeSettingList();
                baseService.updatePatientDrugsTimeSetting(patientDrug, timeSettingList);
            }
        }
        return R.success(patientDrugs);

    }

    @Deprecated
    @Override
    public R<PatientDrugs> handlerUpdate(PatientDrugsUpdateDTO model) {
        PatientDrugs patientDrugs = BeanUtil.toBean(model, PatientDrugs.class);
        if (patientDrugs.getCycle() == 0) {
            patientDrugs.setEndTime(null);
        }
        if (patientDrugs.getBuyDrugsReminderTime() == null) {
            patientDrugs.setBuyDrugsReminderTime(null);
        }
        baseService.updateAllById(patientDrugs);
        return R.success(patientDrugs);
    }


    @ApiOperation("新增患者的用药_20220819版")
    @PostMapping("savePatientDrugs")
    public R<PatientDrugs> savePatientDrugs(@RequestBody @Validated PatientDrugsSaveDTO saveDTO) {
        PatientDrugs patientDrugs = BeanUtil.toBean(saveDTO, PatientDrugs.class);
        List<PatientDrugsTimeSetting> timeSettingList = saveDTO.getPatientDrugsTimeSettingList();
        baseService.savePatientDrugs(patientDrugs, timeSettingList);

        Long chatId = saveDTO.getMessageId();
        if (Objects.nonNull(chatId)) {
            imApi.updateImRemind(chatId);
        }
        return R.success(patientDrugs);
    }


    @ApiOperation("修改患者的用药_20220819版")
    @PostMapping("updatePatientDrugs")
    public R<PatientDrugs> updatePatientDrugs(@RequestBody @Validated PatientDrugsUpdateDTO updateDTO) {
        PatientDrugs patientDrugs = BeanUtil.toBean(updateDTO, PatientDrugs.class);
        List<PatientDrugsTimeSetting> timeSettingList = updateDTO.getPatientDrugsTimeSettingList();
        baseService.updatePatientDrugs(patientDrugs, timeSettingList);
        return R.success(null);
    }


    @ApiOperation("获取患者的用药的详情_20220819版")
    @PostMapping("getPatientDrugsDetails/{patientDrugsId}")
    public R<PatientDrugs> getPatientDrugsDetails(@PathVariable("patientDrugsId") Long patientDrugsId ) {

        PatientDrugs patientDrugs = baseService.getById(patientDrugsId);
        return R.success(patientDrugs);
    }


    @ApiOperation("修改所有用户最新的吃药时间")
    @PostMapping("updateAllPatientDrugsTime")
    public R<String> updateAllPatientDrugsTime(@RequestParam String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        baseService.updateAllPatientDrugsTime();
        return R.success("success");
    }


}
