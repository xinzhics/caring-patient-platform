package com.caring.sass.user.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.CustomGrouping;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.CustomGroupingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CustomGroupingController
 * @Description
 * @Author yangShuai
 * @Date 2021/8/27 11:54
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/customGrouping")
@Api(value = "CustomGroupingController", tags = "自定义分组")
public class CustomGroupingController extends SuperController<CustomGroupingService, Long, CustomGrouping, CustomGroupingPageDTO, CustomGroupingSaveDTO, CustomGroupingUpdateDTO> {



    @ApiOperation("所有的患者基本信息(id, Name, 头像, 备注, ImAccount)")
    @PostMapping("/findAllPatientBaseInfo/{customGroupingId}")
    public R<List<Patient>> findAllPatientBaseInfo(@PathVariable("customGroupingId") Long customGroupingId) {

        List<Patient> patients = baseService.findAllPatientBaseInfo(customGroupingId);

        return R.success(patients);
    }


    @ApiOperation("查询小组下的患者基本信息(id, Name, 头像, 备注, ImAccount)")
    @PostMapping("/findPatientBaseInfo/{customGroupingId}")
    public R<IPage<Patient>> findPatientBaseInfo(@PathVariable("customGroupingId") Long customGroupingId,
            @RequestBody @Validated PageParams<Object> params) {
        IPage<Patient> buildPage = params.buildPage();
        IPage<Patient> patientIPage = baseService.findPatientBaseInfo(buildPage, customGroupingId);
        return R.success(patientIPage);
    }

    @ApiOperation("查询医助患者的列表并按首字母返回")
    @PostMapping("pageNursingPatient")
    public R<IPage<JSONObject>> pageDoctorPatient(@RequestBody PageParams<NursingCustomGroupPatientPageDTO> pageParams) {
        NursingCustomGroupPatientPageDTO model = pageParams.getModel();
        IPage<Patient> iPage = pageParams.buildPage();
        IPage<JSONObject> page = baseService.pageNursingPatient(iPage, model);
        return R.success(page);
    }

    @ApiOperation("删除群组中一个患者")
    @DeleteMapping("/deletePatient/{customGroupingId}")
    public R<Boolean> deletePatient(@PathVariable("customGroupingId") Long customGroupingId,
                                    @RequestParam("patientId") Long patientId) {
        baseService.deletePatient(customGroupingId, patientId);
        return R.success(true);
    }

    @ApiOperation("删除群组")
    @DeleteMapping("/deleteGroup/{id}")
    public R<Boolean> deleteGroup(@PathVariable("id") Long id) {

        baseService.removeById(id);
        return R.success(true);
    }

    @ApiOperation("更新医助自定义小组名称和患者")
    @PutMapping("/updateGroupPatient/{customGroupId}")
    public R<CustomGrouping> updateGroupPatient(
            @PathVariable("customGroupId") @NotNull Long customGroupId,
            @Validated @RequestBody NursingCustomGroupUpdateDTO nursingCustomGroupUpdateDTO) {
        CustomGrouping grouping = baseService.updateGroupPatient(customGroupId, nursingCustomGroupUpdateDTO);
        return R.success(grouping);
    }

    @ApiOperation("新增医助自定义小组")
    @PostMapping("/createGroupPatient")
    public R<CustomGrouping> createGroupPatient(@Validated @RequestBody NursingCustomGroupUpdateDTO nursingCustomGroupUpdateDTO) {
        CustomGrouping grouping = baseService.createGroupPatient(nursingCustomGroupUpdateDTO);
        return R.success(grouping);
    }


    @ApiOperation("医助所有小组列表统计小组内非取关患者数量")
    @PostMapping("/queryAll/{nursingId}")
    public R<List<CustomGrouping>> queryAll(@PathVariable("nursingId") Long nursingId) {
        List<Integer> patientStatus = new ArrayList<>();
        patientStatus.add(Patient.PATIENT_SUBSCRIBE);
        patientStatus.add(Patient.PATIENT_SUBSCRIBE_NORMAL);
        List<CustomGrouping> customGroupings = baseService.queryAllAndCountPatientNumber(nursingId, patientStatus);
        return R.success(customGroupings);
    }
}
