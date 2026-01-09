package com.caring.sass.nursing.controller.information;


import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.dto.information.CompletenessInformationPageDTO;
import com.caring.sass.nursing.dto.information.CompletenessInformationPatientEditDto;
import com.caring.sass.nursing.dto.information.CompletenessInformationSaveDTO;
import com.caring.sass.nursing.dto.information.CompletenessInformationUpdateDTO;
import com.caring.sass.nursing.entity.information.CompletenessInformation;
import com.caring.sass.nursing.qo.CompletenessInformationQo;
import com.caring.sass.nursing.qo.IntervalQo;
import com.caring.sass.nursing.qo.NotificationQo;
import com.caring.sass.nursing.qo.ReminderQo;
import com.caring.sass.nursing.service.drugs.DrugsResultInformationService;
import com.caring.sass.nursing.service.form.IndicatorsResultInformationService;
import com.caring.sass.nursing.service.information.CompletenessInformationService;
import com.caring.sass.nursing.service.traceInto.TraceIntoFormResultLastPushTimeService;
import com.caring.sass.nursing.service.unfinished.UnfinishedPatientRecordService;
import com.caring.sass.nursing.vo.NotificaVo;
import com.caring.sass.tenant.api.PatientManageMenuApi;
import com.caring.sass.tenant.entity.router.PatientManageMenu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 患者信息完整度概览表
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/completenessInformation")
@Api(value = "CompletenessInformation", tags = "患者信息完整度概览表")
//@PreAuth(replace = "completenessInformation:")
public class CompletenessInformationController extends SuperController<CompletenessInformationService, Long, CompletenessInformation, CompletenessInformationPageDTO, CompletenessInformationSaveDTO, CompletenessInformationUpdateDTO> {

    @Autowired
    IndicatorsResultInformationService indicatorsResultInformationService;

    @Autowired
    DrugsResultInformationService drugsResultInformationService;

    @Autowired
    TraceIntoFormResultLastPushTimeService traceIntoFormResultLastPushTimeService;

    @Autowired
    UnfinishedPatientRecordService unfinishedPatientRecordService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<CompletenessInformation> completenessInformationList = list.stream().map((map) -> {
            CompletenessInformation completenessInformation = CompletenessInformation.builder().build();
            //TODO 请在这里完成转换
            return completenessInformation;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(completenessInformationList));
    }

    /**
     * 信息完整度类别接口
     *
     * @return
     */
    @ApiOperation(value = "信息完整度类别搜索接口")
    @PostMapping("search")
    public R<IPage<CompletenessInformation>> search(@RequestBody PageParams<CompletenessInformationQo> params) {
        IPage<CompletenessInformation> selectList = baseService.selectList(params);
        return R.success(selectList);
    }

    @Autowired
    PatientManageMenuApi patientManageMenuApi;

    @ApiOperation(value = "app首页统计患者管理数据")
    @GetMapping("appCountHome/{nursingId}")
    public R<Integer> appCountHome(@PathVariable("nursingId") Long nursingId) {

        String tenant = BaseContextHandler.getTenant();
        R<List<PatientManageMenu>> queryList = patientManageMenuApi.queryList(tenant);
        Set<String> menuTypes = new HashSet<>();
        if (queryList.getIsSuccess()) {
            List<PatientManageMenu> patientManageMenus = queryList.getData();
            for (PatientManageMenu manageMenu : patientManageMenus) {
                if (manageMenu.getShowStatus() == 1) {
                    menuTypes.add(manageMenu.getMenuType());
                }
            }
        }
        int total = 0;
        if (menuTypes.contains(PatientManageMenu.INFORMATION_INTEGRITY)) {
            Integer integer = baseService.selectCount(nursingId);
            if (integer != null) {
                total += integer;
            }
        }
        if (menuTypes.contains(PatientManageMenu.MONITOR_DATA)) {
            Integer unusualPatients = indicatorsResultInformationService.monitorUnusualPatients(tenant);
            if (unusualPatients != null) {
                total += unusualPatients;
            }
        }
        if (menuTypes.contains(PatientManageMenu.MEDICATION_WARNING)) {
            Integer drugsNumber = drugsResultInformationService.getDrugsNumber();
            if (drugsNumber != null) {
                total += drugsNumber;
            }
        }
        if (menuTypes.contains(PatientManageMenu.EXCEPTION_OPTION_TRACKING)) {
            int nursingHandleNumber = traceIntoFormResultLastPushTimeService.countNursingHandleNumber(nursingId);
            total += nursingHandleNumber;
        }
        // 未完成跟踪
        if (menuTypes.contains(PatientManageMenu.NOT_FINISHED_TRACKING)) {
            int nursingHandleNumber = unfinishedPatientRecordService.countNursingHandleNumber(nursingId);
            total += nursingHandleNumber;
        }
        return R.success(total);
    }

    @ApiOperation(value = "app患者管理平台首页各项的数量统计")
    @GetMapping("countPatientManageNumber/{nursingId}")
    public R<JSONObject> countPatientManageNumber(@PathVariable("nursingId") Long nursingId) {

        Integer integer = baseService.selectCount(nursingId);
        Integer unusualPatients = indicatorsResultInformationService.monitorUnusualPatients(BaseContextHandler.getTenant());
        Integer drugsNumber = drugsResultInformationService.getDrugsNumber();
        int nursingHandleNumber = traceIntoFormResultLastPushTimeService.countNursingHandleNumber(nursingId);
        int countedNursingHandleNumber = unfinishedPatientRecordService.countNursingHandleNumber(nursingId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set(PatientManageMenu.INFORMATION_INTEGRITY, integer);
        jsonObject.set(PatientManageMenu.MONITOR_DATA, unusualPatients);
        jsonObject.set(PatientManageMenu.MEDICATION_WARNING, drugsNumber);
        jsonObject.set(PatientManageMenu.EXCEPTION_OPTION_TRACKING, nursingHandleNumber);
        jsonObject.set(PatientManageMenu.NOT_FINISHED_TRACKING, countedNursingHandleNumber);
        return R.success(jsonObject);
    }

    @ApiOperation(value = "统计信息完整度不完整的患者数量")
    @Deprecated
    @GetMapping("countNoComplete/{nursingId}")
    public R<Integer> countNoComplete(@PathVariable("nursingId") Long nursingId) {
        Integer integer = baseService.selectCount(nursingId);
        return R.success(integer);
    }


    @ApiOperation(value = "发送通知消息群发")
    @PostMapping("sendNotification")
    public R<String> sendMessageReminder(@RequestBody ReminderQo reminder) {
        baseService.sendMessageReminder(reminder);
        return R.success("success");
    }

    @ApiOperation(value = "查询当前发送通知的总人数")
    @GetMapping("notificationNum")
    public R<NotificaVo> notification(@RequestParam Integer min, @RequestParam Integer max) {
        IntervalQo intervalQo = new IntervalQo();
        intervalQo.setMin(min);
        intervalQo.setMax(max);
        return baseService.notification(intervalQo);
    }

    @ApiOperation(value = "发送通知消息-单条发送")
    @PostMapping("sendOne")
    public R<String> sendOnd(@RequestBody NotificationQo notificationQo) {
        return baseService.sendOne(notificationQo.getPatientId());
    }

    @ApiOperation(value = "查询患者信息部完善的字段")
    @GetMapping("/anno/findIncompleteInformation")
    public R<CompletenessInformation> findIncompleteInformation(@RequestParam Long patientId, @RequestParam String code) {
        CompletenessInformation incompleteInformation = baseService.findIncompleteInformation(patientId, code);
        return R.success(incompleteInformation);
    }


    @ApiOperation(value = "查询患者字段和表单的字段")
    @GetMapping("/findIncompleteInformation")
    public R<CompletenessInformationPatientEditDto> findCompletenessFormField(@RequestParam Long patientId) {
        CompletenessInformationPatientEditDto incompleteInformation = baseService.findCompletenessFormField(patientId);
        return R.success(incompleteInformation);
    }

    @ApiOperation(value = "患者保存信息完整度字段结果")
    @PostMapping("/updateIncompleteInformationField")
    public R<String> updateIncompleteInformationField(@RequestBody CompletenessInformationPatientEditDto editDto) {
        baseService.updateIncompleteInformationField(editDto);
        return R.success("success");
    }




}
