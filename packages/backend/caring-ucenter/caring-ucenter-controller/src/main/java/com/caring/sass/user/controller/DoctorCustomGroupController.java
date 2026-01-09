package com.caring.sass.user.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.dto.DoctorCustomGroupPageDTO;
import com.caring.sass.user.dto.DoctorCustomGroupPatientPageDTO;
import com.caring.sass.user.dto.DoctorCustomGroupSaveDTO;
import com.caring.sass.user.dto.DoctorCustomGroupUpdateDTO;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.DoctorCustomGroup;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorCustomGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.taskdefs.War;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 医生的自定义小组
 * </p>
 *
 * @author yangshuai
 * @date 2022-08-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/doctorCustomGroup")
@Api(value = "DoctorCustomGroup", tags = "医生的自定义小组")
public class DoctorCustomGroupController extends SuperController<DoctorCustomGroupService, Long, DoctorCustomGroup, DoctorCustomGroupPageDTO, DoctorCustomGroupSaveDTO, DoctorCustomGroupUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<DoctorCustomGroup> doctorCustomGroupList = list.stream().map((map) -> {
            DoctorCustomGroup doctorCustomGroup = DoctorCustomGroup.builder().build();
            //TODO 请在这里完成转换
            return doctorCustomGroup;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(doctorCustomGroupList));
    }

    @ApiOperation("新增自定义小组")
    @PostMapping("saveDoctorGroup")
    public R<DoctorCustomGroup> saveDoctorGroup(@RequestBody DoctorCustomGroupSaveDTO saveDTO) {
        DoctorCustomGroup customGroup = baseService.saveDoctorGroupAndPatient(saveDTO);
        return R.success(customGroup);
    }


    @ApiOperation("修改自定义小组")
    @PostMapping("updateDoctorGroup")
    public R<DoctorCustomGroup> updateDoctorGroup(@RequestBody DoctorCustomGroupUpdateDTO updateDTO) {
        DoctorCustomGroup customGroup = baseService.updateDoctorGroupPatient(updateDTO);
        return R.success(customGroup);
    }


    @ApiOperation("删除小组内的某个患者")
    @PostMapping("deleteDoctorGroupPatient")
    public R<Boolean> deleteDoctorGroupPatient(@RequestParam("groupId") Long groupId,
                                                         @RequestParam("patientId") Long patientId) {
        boolean status = baseService.deleteDoctorGroupPatient(groupId, patientId);
        return R.success(status);
    }


    @ApiOperation("删除小组")
    @PostMapping("deleteDoctorGroup")
    public R<Boolean> deleteDoctorGroup(@RequestParam("groupId") Long groupId) {
        boolean status = baseService.removeById(groupId);
        return R.success(status);
    }

    @ApiOperation("小组内患者的列表")
    @PostMapping("pageGroupPatient")
    public R<IPage<Patient>> pageGroupPatient(@RequestBody PageParams<DoctorCustomGroupPatientPageDTO> pageParams) {
        DoctorCustomGroupPatientPageDTO model = pageParams.getModel();
        IPage<Patient> iPage = pageParams.buildPage();
        baseService.pageGroupPatient(iPage, model);
        return R.success(iPage);
    }


    @ApiOperation("医助下医生列表")
    @PostMapping("pageGroupDoctor")
    public R<List<Doctor>> pageGroupDoctor(@RequestBody DoctorCustomGroupPageDTO pageParams) {
        List<Doctor> doctors =  baseService.queryGroupDoctor(pageParams);
        return R.success(doctors);
    }

    @ApiOperation("查询医生患者的列表并按首字母返回")
    @PostMapping("pageDoctorPatient")
    public R<IPage<JSONObject>> pageDoctorPatient(@RequestBody PageParams<DoctorCustomGroupPatientPageDTO> pageParams) {
        DoctorCustomGroupPatientPageDTO model = pageParams.getModel();
        IPage<Patient> iPage = pageParams.buildPage();
        IPage<JSONObject> page = baseService.pageDoctorPatient(iPage, model);
        return R.success(page);
    }

    @ApiOperation("医生的小组列表")
    @PostMapping("list")
    public R<List<DoctorCustomGroup>> list(@RequestParam Long doctorId) {
        List<DoctorCustomGroup> list = baseService.list(Wraps.<DoctorCustomGroup>lbQ().eq(DoctorCustomGroup::getDoctorId, doctorId));
        return R.success(list);
    }

    @ApiOperation("搜索医生的小组")
    @GetMapping("queryDoctorCustomGroup")
    public R<List<Long>> queryDoctorCustomGroup(@RequestParam(name = "doctorId") Long doctorId,
                                          @RequestParam(name = "groupName") String groupName) {
        LbqWrapper<DoctorCustomGroup> wrapper = Wraps.<DoctorCustomGroup>lbQ()
                .select(SuperEntity::getId, DoctorCustomGroup::getGroupName)
                .eq(DoctorCustomGroup::getDoctorId, doctorId)
                .like(DoctorCustomGroup::getGroupName, groupName);
        List<DoctorCustomGroup> list = baseService.list(wrapper);
        if (CollUtil.isNotEmpty(list)) {
            List<Long> collect = list.stream().map(SuperEntity::getId).collect(Collectors.toList());
            return R.success(collect);
        } else {
            return R.success(new ArrayList<>());
        }
    }


    @ApiOperation("搜索医生的小组")
    @PostMapping("queryDoctorCustomGroup")
    public R<List<DoctorCustomGroup>> queryDoctorCustomGroup(@RequestBody List<Long> groupIds) {

        List<DoctorCustomGroup> groups = baseService.list(Wraps.<DoctorCustomGroup>lbQ()
                .in(SuperEntity::getId, groupIds)
                .select(SuperEntity::getId, DoctorCustomGroup::getGroupName));
        return R.success(groups);
    }


}
