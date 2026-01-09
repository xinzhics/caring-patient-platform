package com.caring.sass.user.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.dto.DoctorNoReadGroupDoctorMsgDto;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.DoctorGroup;
import com.caring.sass.user.entity.DoctorNoReadGroupDoctorMsg;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorGroupService;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName DoctorGroupController
 * @Description
 * @Author yangShuai
 * @Date 2021/9/14 16:48
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/doctorGroup")
@Api(value = "DoctorGroup", tags = "医生小组关系")
public class DoctorGroupController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    PatientService patientService;

    @Autowired
    private DoctorGroupService doctorGroupService;

    @ApiOperation("查看同组医生的信息不包含自己")
    @GetMapping(value = "/getGroupDoctor/baseInfo")
    public R<List<Doctor>> getGroupDoctor(@RequestParam("doctorId") Long doctorId) {

        List<Doctor> doctorList = doctorService.getGroupDoctorBaseInfoNoMe(doctorId);
        return R.success(doctorList);

    }

    /**
     * 查询医生所在小组内所有的医生ID
     * @return
     */
    @ApiOperation("获取小组内的所有医生ID")
    @GetMapping(value = "/findGroupDoctorIdByGroupId")
    public R<List<Long>> findGroupDoctorIdByGroupId(@RequestParam("groupId") Long groupId) {

        List<DoctorGroup> doctorGroups = doctorGroupService.list(Wraps.<DoctorGroup>lbQ().eq(DoctorGroup::getGroupId, groupId).select(DoctorGroup::getDoctorId));
        if (CollUtil.isEmpty(doctorGroups)) {
            return R.success(new ArrayList<>(0));
        }
        return R.success(doctorGroups.stream().map(DoctorGroup::getDoctorId).collect(Collectors.toList()));
    }

    /**
     * 查询医生所在小组内所有的医生ID
     * @return
     */
    @ApiOperation("获取医生的ID所在小组内的所有医生ID")
    @GetMapping(value = "/findGroupDoctorIdByDoctorId")
    public R<List<Long>> findGroupDoctorIdByDoctorId(@RequestParam("doctorId") Long doctorId) {

        List<Long> doctorIds = doctorGroupService.findGroupDoctorIdByDoctorId(doctorId);
        return R.success(doctorIds);
    }



    @ApiOperation("获取医生的一个小组(当前版本医生只有一个小组)")
    @GetMapping(value = "/getGroupDoctorOne")
    public R<DoctorGroup> getGroupDoctorOne(@RequestParam("doctorId") Long doctorId) {

        LbqWrapper<DoctorGroup> wrapper = Wraps.<DoctorGroup>lbQ()
                .eq(DoctorGroup::getDoctorId, doctorId);

        List<DoctorGroup> doctorGroups = doctorGroupService.list(wrapper);
        if (CollectionUtils.isEmpty(doctorGroups)) {
            return R.success(null);
        } else {
            return R.success(doctorGroups.get(0));
        }
    }

    @ApiOperation("查询所在小组内的医生, 并返回查看患者消息状态")
    @GetMapping(value = "/getGroupDoctor/baseInfo/readMsgStatus")
    public R<List<DoctorNoReadGroupDoctorMsgDto>> getDoctorNoReadGroupDoctorMsgDto(@RequestParam("doctorId") Long doctorId) {

        List<Doctor> doctorList = doctorService.getGroupDoctorBaseInfoNoMe(doctorId);
        List<DoctorNoReadGroupDoctorMsgDto> readStauts = doctorGroupService.setReadStauts(doctorList, doctorId);
        return R.success(readStauts);
    }

    @ApiOperation("修改医生不想查看某医生患者消息")
    @PostMapping(value = "/updateDoctorNoReadGroupDoctor")
    public R<Boolean> updateDoctorNoReadGroupDoctor(@RequestBody DoctorNoReadGroupDoctorMsg groupDoctorMsg) {

        Boolean saveStatus = doctorGroupService.saveDoctorNoReadGroupDoctor(groupDoctorMsg);
        return R.success(saveStatus);

    }

    @ApiOperation("查询医生进入小组的时间")
    @GetMapping(value = "/getDoctorGroupTime")
    public R<DoctorGroup> getDoctorGroupTime(@RequestParam("doctorId") Long doctorId,
                                             @RequestParam("patientImAccount") String patientImAccount) {

        // 查询医生是否有小组
        LbqWrapper<DoctorGroup> wrapper = Wraps.<DoctorGroup>lbQ()
                .select(DoctorGroup::getDoctorId, DoctorGroup::getGroupId, DoctorGroup::getJoinGroupTime)
                .eq(DoctorGroup::getDoctorId, doctorId);
        List<DoctorGroup> groups = doctorGroupService.list(wrapper);
        if (CollectionUtils.isEmpty(groups)) {
            return R.success(null);
        }
        // 医生有小组  由于要兼容医生可能出现多个小组的情况， 故需要使用 患者 医生小组
        // 根据患者的im账号找到患者的医生所在的小组。 然后确定医生 进入这个小组的 时间
        Patient patient = patientService.getOne(Wraps.<Patient>lbQ()
                .eq(Patient::getImAccount, patientImAccount)
                .select(SuperEntity::getId, Patient::getDoctorId).last(" limit 0,1 "));
        if (Objects.isNull(patient)) {
            return R.success(null);
        }
        // 医生是患者的医生。不返回小组信息
        if (doctorId.equals(patient.getDoctorId())) {
            return R.success(null);
        }
        LbqWrapper<DoctorGroup> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(DoctorGroup::getDoctorId, patient.getDoctorId());
        lbqWrapper.select(DoctorGroup::getDoctorId, DoctorGroup::getGroupId, DoctorGroup::getJoinGroupTime);
        List<DoctorGroup> groupList = doctorGroupService.list(lbqWrapper);
        if (CollectionUtils.isEmpty(groupList)) {
            return R.success(null);
        }
        Set<Long> groupIds = groupList.stream().map(DoctorGroup::getGroupId).collect(Collectors.toSet());
        for (DoctorGroup group : groups) {
            if (groupIds.contains(group.getGroupId())) {
                return R.success(group);
            }
        }
        return R.success(null);
    }

    @ApiOperation("查询医生愿意看的医生患者, 和医生入组时间")
    @GetMapping(value = "/getDoctorGroup/otherDoctor")
    public R<List<DoctorGroup>> getDoctorGroupOtherDoctor(@RequestParam("doctorId") Long doctorId) {

        List<DoctorGroup> groupList = doctorGroupService.getDoctorGroupOtherDoctor(doctorId);
        return R.success(groupList);

    }


    @ApiOperation("获取医生所在小组愿意接收消息的医生im账号")
    @GetMapping("getGroupDoctorListReadMyMessage")
    public R<List<String>> getGroupDoctorListReadMyMessage(@RequestParam("doctorId") Long doctorId) {

        List<Doctor> readMyMessage = doctorGroupService.getGroupDoctorListReadMyMessage(doctorId);
        if (CollUtil.isNotEmpty(readMyMessage)) {
            List<String> imAccount = new ArrayList<>();
            for (Doctor doctor : readMyMessage) {
                if (StrUtil.isNotEmpty(doctor.getOpenId()) && doctor.getImMsgStatus() != null && doctor.getImMsgStatus() == 1) {
                    imAccount.add(doctor.getImAccount());
                }
            }
            return R.success(imAccount);
        }
        return R.success(null);

    }

}
