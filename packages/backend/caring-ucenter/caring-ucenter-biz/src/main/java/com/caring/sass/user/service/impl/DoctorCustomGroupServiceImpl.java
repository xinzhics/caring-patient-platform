package com.caring.sass.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.LetterUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.user.dao.*;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.DoctorCustomGroupService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ArrayStack;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 医生的自定义小组
 * </p>
 *
 * @author yangshuai
 * @date 2022-08-11
 */
@Slf4j
@Service

public class DoctorCustomGroupServiceImpl extends SuperServiceImpl<DoctorCustomGroupMapper, DoctorCustomGroup> implements DoctorCustomGroupService {

    @Autowired
    DoctorMapper doctorMapper;

    @Autowired
    PatientMapper patientMapper;

    @Autowired
    DoctorGroupMapper doctorGroupMapper;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    DoctorCustomGroupPatientMapper doctorCustomGroupPatientMapper;


    @Override
    public boolean save(DoctorCustomGroup model) {
        super.save(model);
        UpdateWrapper<Doctor> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("has_group", 1);
        updateWrapper.eq("id", model.getDoctorId());
        doctorMapper.update(new Doctor(), updateWrapper);
        return true;
    }

    /**
     * 新增时 保存小组名称。 保存小组内的患者
     * @param saveDTO
     * @return
     */
    @Transactional
    @Override
    public DoctorCustomGroup saveDoctorGroupAndPatient(DoctorCustomGroupSaveDTO saveDTO) {
        Long doctorId = saveDTO.getDoctorId();
        String groupName = saveDTO.getGroupName();
        Set<Long> patientIds = saveDTO.getJoinPatientIds();
        DoctorCustomGroup customGroup = new DoctorCustomGroup();
        customGroup.setDoctorId(doctorId);
        customGroup.setGroupName(groupName);
        if (CollUtil.isNotEmpty(patientIds)) {
            customGroup.setGroupNumberTotal(patientIds.size());
        } else {
            customGroup.setGroupNumberTotal(0);
        }
        save(customGroup);
        if (CollUtil.isNotEmpty(patientIds)) {
            Long customGroupId = customGroup.getId();
            List<DoctorCustomGroupPatient> doctorCustomGroupPatients = new ArrayList<>(patientIds.size());
            for (Long patientId : patientIds) {
                doctorCustomGroupPatients.add(
                        DoctorCustomGroupPatient.builder()
                        .patientId(patientId)
                        .doctorCustomGroupId(customGroupId)
                        .build());
            }
            doctorCustomGroupPatientMapper.insertBatchSomeColumn(doctorCustomGroupPatients);
        }
        return customGroup;

    }

    @Transactional
    @Override
    public DoctorCustomGroup updateDoctorGroupPatient(DoctorCustomGroupUpdateDTO groupUpdateDTO) {
        Long id = groupUpdateDTO.getId();
        String groupName = groupUpdateDTO.getGroupName();
        Set<Long> joinPatientIds = groupUpdateDTO.getJoinPatientIds();
        Set<Long> removePatientIds = groupUpdateDTO.getRemovePatientIds();
        if (CollUtil.isNotEmpty(joinPatientIds) && CollUtil.isNotEmpty(removePatientIds)) {
            for (Long patientId : joinPatientIds) {
                if (removePatientIds.contains(patientId)) {
                    throw new BizException("不能同时存在于加入和移出状态中");
                }
            }
        }
        if (CollUtil.isNotEmpty(joinPatientIds)) {
            doctorCustomGroupPatientMapper.delete(Wraps.<DoctorCustomGroupPatient>lbQ()
                    .eq(DoctorCustomGroupPatient::getDoctorCustomGroupId, id)
                    .in(DoctorCustomGroupPatient::getPatientId, joinPatientIds));
            List<DoctorCustomGroupPatient> doctorCustomGroupPatients = new ArrayList<>(joinPatientIds.size());
            for (Long patientId : joinPatientIds) {
                doctorCustomGroupPatients.add(
                        DoctorCustomGroupPatient.builder()
                                .patientId(patientId)
                                .doctorCustomGroupId(id)
                                .build());
            }
            doctorCustomGroupPatientMapper.insertBatchSomeColumn(doctorCustomGroupPatients);
        }
        if (CollUtil.isNotEmpty(removePatientIds)) {
            doctorCustomGroupPatientMapper.delete(Wraps.<DoctorCustomGroupPatient>lbQ()
            .eq(DoctorCustomGroupPatient::getDoctorCustomGroupId, id)
            .in(DoctorCustomGroupPatient::getPatientId, removePatientIds));
        }
        Integer count = doctorCustomGroupPatientMapper.selectCount(Wraps.<DoctorCustomGroupPatient>lbQ()
                .eq(DoctorCustomGroupPatient::getDoctorCustomGroupId, id));
        DoctorCustomGroup doctorCustomGroup = new DoctorCustomGroup();
        doctorCustomGroup.setId(id);
        doctorCustomGroup.setGroupName(groupName);
        doctorCustomGroup.setGroupNumberTotal(count);
        super.updateById(doctorCustomGroup);
        return doctorCustomGroup;
    }


    @Transactional
    @Override
    public boolean deleteDoctorGroupPatient(Long groupId, Long patientId) {
        DoctorCustomGroup customGroup = baseMapper.selectById(groupId);
        doctorCustomGroupPatientMapper.delete(Wraps.<DoctorCustomGroupPatient>lbQ()
                .eq(DoctorCustomGroupPatient::getDoctorCustomGroupId, groupId)
                .eq(DoctorCustomGroupPatient::getPatientId, patientId));
        Integer count = doctorCustomGroupPatientMapper.selectCount(Wraps.<DoctorCustomGroupPatient>lbQ()
                .eq(DoctorCustomGroupPatient::getDoctorCustomGroupId, groupId));
        customGroup.setGroupNumberTotal(count);
        baseMapper.updateById(customGroup);
        return false;
    }


    @Transactional
    @Override
    public boolean removeById(Serializable id) {

        doctorCustomGroupPatientMapper.delete(Wraps.<DoctorCustomGroupPatient>lbQ()
                .eq(DoctorCustomGroupPatient::getDoctorCustomGroupId, id));
        DoctorCustomGroup doctorCustomGroup = baseMapper.selectById(id);
        super.removeById(id);
        Integer integer = baseMapper.selectCount(Wraps.<DoctorCustomGroup>lbQ().eq(DoctorCustomGroup::getDoctorId, doctorCustomGroup.getDoctorId()));
        if (integer == null || integer == 0) {
            UpdateWrapper<Doctor> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("has_group", 0);
            updateWrapper.eq("id", doctorCustomGroup.getDoctorId());
            doctorMapper.update(new Doctor(), updateWrapper);
        }
        return true;
    }

    /**
     * 分页查询小组内的患者
     * @param iPage
     * @param model
     */
    @Override
    public void pageGroupPatient(IPage<Patient> iPage, DoctorCustomGroupPatientPageDTO model) {
        String patientName = model.getPatientName();
        LbqWrapper<Patient> wrapper = Wraps.<Patient>lbQ().eq(Patient::getDoctorId, model.getDoctorId());
        if (StrUtil.isNotEmpty(patientName)) {
            wrapper.like(Patient::getName, patientName);
        }
        wrapper.apply(" id in (select patient_id from u_user_doctor_custom_group_patient where doctor_custom_group_id = "+ model.getDoctorCustomGroupId()+") ");
        patientMapper.selectPage(iPage, wrapper);
    }

    /**
     * 查询小组内的患者列表。并设置是否选中
     * @param iPage
     * @param model
     * @return
     */
    public IPage<JSONObject> pageDoctorPatient(IPage<Patient>  iPage, DoctorCustomGroupPatientPageDTO model) {
        Long doctorCustomGroupId = model.getDoctorCustomGroupId();
        Long doctorId = model.getDoctorId();
        String patientName = model.getPatientName();
        LbqWrapper<Patient> wrapper = Wraps.<Patient>lbQ().eq(Patient::getDoctorId, doctorId);
        if (StrUtil.isNotEmpty(patientName)) {
            wrapper.like(Patient::getName, patientName);
        }
        wrapper.orderByAsc(Patient::getNameFirstLetter);
        wrapper.select(SuperEntity::getId, Patient::getName, Patient::getAvatar, Patient::getImAccount, Patient::getDoctorId,
                Patient::getRemark, Patient::getDoctorRemark,
                Patient::getNameFirstLetter, Patient::getDoctorRemark);
        patientMapper.selectPage(iPage, wrapper);
        List<Patient> records = iPage.getRecords();
        IPage<JSONObject> resultPage = new Page<>();
        resultPage.setSize(iPage.getSize());
        resultPage.setTotal(iPage.getTotal());
        resultPage.setCurrent(iPage.getCurrent());
        resultPage.setPages(iPage.getPages());
        if (CollUtil.isEmpty(records)) {
            return resultPage;
        }
        // 查询小组下 这些患者是否选中

        List<Long> patientIds = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
        List<DoctorCustomGroupPatient> groupPatients = new ArrayList<>();
        if (Objects.nonNull(doctorCustomGroupId)) {
            groupPatients = doctorCustomGroupPatientMapper.selectList(Wraps.<DoctorCustomGroupPatient>lbQ()
                    .eq(DoctorCustomGroupPatient::getDoctorCustomGroupId, doctorCustomGroupId)
                    .in(DoctorCustomGroupPatient::getPatientId, patientIds));
        }
        List<DoctorCustomGroupPatientModel> patientModels = new ArrayList<>(records.size());
        if (CollUtil.isEmpty(groupPatients)) {
            groupPatients = new ArrayList<>();
        }
        final Set<Long> checkedPatientIdSet = groupPatients.stream().map(DoctorCustomGroupPatient::getPatientId).collect(Collectors.toSet());
        records.forEach(item -> {
            DoctorCustomGroupPatientModel patientModel = new DoctorCustomGroupPatientModel();
            BeanUtils.copyProperties(item, patientModel);
            patientModel.setId(item.getId());
            patientModel.setChecked(checkedPatientIdSet.contains(item.getId()));
            patientModels.add(patientModel);
        });
        // 分组
        Map<String, List<DoctorCustomGroupPatientModel>>
                stringListMap = patientModels.stream().collect(Collectors.groupingBy(DoctorCustomGroupPatientModel::getNameFirstLetter));
        // 按 大写 英文字母 表 从map 中获取分组后的数据
        List<JSONObject> list = new ArrayList<>();
        JSONObject jsonObject;
        for (String s : LetterUtil.getInstance().ENGLISH_ALPHABET) {
            List<DoctorCustomGroupPatientModel> patientModelList = stringListMap.get(s);
            if (CollUtil.isNotEmpty(patientModelList)) {
                jsonObject = new JSONObject();
                jsonObject.set("nameFirstLetter", s);
                jsonObject.set("values", patientModelList);
                list.add(jsonObject);
            }
        }
        resultPage.setRecords(list);
        return resultPage;
    }

    /**
     * 医助下的医生列表
     * @param model
     * @return
     */
    @Override
    public List<Doctor> queryGroupDoctor(DoctorCustomGroupPageDTO model) {
        Long doctorId = model.getDoctorId();

        Long groupId = model.getGroupId();

        Long nursingId = model.getNursingId();
        LbqWrapper<Doctor> wrapper = Wraps.<Doctor>lbQ().eq(Doctor::getNursingId, nursingId);
        if (Objects.nonNull(doctorId)) {
            wrapper.eq(SuperEntity::getId, doctorId);
        } else if (Objects.nonNull(groupId)) {
            wrapper.apply(" id in (select doctor_id from u_user_doctor_group where group_id = "+ groupId+" ) ");
        }
        wrapper.orderByDesc(Doctor::getHasGroup);
        wrapper.orderByAsc(Doctor::getCreateTime);

        wrapper.select(SuperEntity::getId, Doctor::getName, Doctor::getAvatar, Doctor::getHasGroup,
                Doctor::getIndependence, SuperEntity::getCreateTime, Doctor::getAppointmentReview, Doctor::getCloseAppoint);
        List<Doctor> doctors = doctorMapper.selectList(wrapper);
        // 查询 医生所属的小组
        setDoctorGroupInfo(doctors);
        return doctors;
    }


    /**
     * @title 给医生列表设置医生的独立医生状态和 所在小组名称
     * @author 杨帅
     * @updateTime 2023/4/3 15:46
     * @throws
     */
    @Override
    public void setDoctorGroupInfo(List<Doctor> doctors) {
        if (CollUtil.isNotEmpty(doctors)) {
            List<Long> doctorIds = doctors.stream().filter(item -> item.getIndependence() == 0)
                    .map(SuperEntity::getId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(doctorIds)) {
                List<DoctorGroup> doctorGroups = doctorGroupMapper.selectList(Wraps.<DoctorGroup>lbQ()
                        .in(DoctorGroup::getDoctorId, doctorIds));
                if (Objects.nonNull(doctorGroups)) {
                    Set<Long> collect = doctorGroups.stream().map(DoctorGroup::getGroupId).collect(Collectors.toSet());
                    if (CollUtil.isNotEmpty(collect)) {
                        List<Group> groups = groupMapper.selectBatchIds(collect);
                        if (Objects.nonNull(groups)) {
                            Map<Long, Long> doctorGroup = doctorGroups.stream()
                                    .collect(Collectors.toMap(DoctorGroup::getDoctorId, DoctorGroup::getGroupId, (o1, o2) -> o1));
                            Map<Long, String> groupNameMap = groups.stream().collect(Collectors.toMap(SuperEntity::getId, Group::getName));
                            for (Doctor doctor : doctors) {
                                Long doctorGroupId = doctorGroup.get(doctor.getId());
                                if (Objects.nonNull(doctorGroupId)) {
                                    String s = groupNameMap.get(doctorGroupId);
                                    doctor.setGroupName(s);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @Override
    public void removePatient(Long patientId, Long patientDoctorId) {

        // 查询就的医生的小组
        List<DoctorCustomGroupPatient> groupPatients = doctorCustomGroupPatientMapper.selectList(Wraps.<DoctorCustomGroupPatient>lbQ()
                .eq(DoctorCustomGroupPatient::getPatientId, patientId));
        if (CollUtil.isEmpty(groupPatients)) {
            return;
        }
        for (DoctorCustomGroupPatient groupPatient : groupPatients) {
            Long customGroupId = groupPatient.getDoctorCustomGroupId();
            DoctorCustomGroup customGroup = baseMapper.selectById(customGroupId);
            if (Objects.nonNull(customGroup) && Objects.nonNull(customGroup.getGroupNumberTotal())) {
                customGroup.setGroupNumberTotal(customGroup.getGroupNumberTotal() -1);
                if (customGroup.getGroupNumberTotal() <= 0) {
                    customGroup.setGroupNumberTotal(0);
                }
                baseMapper.updateById(customGroup);
            }
        }
        doctorCustomGroupPatientMapper.delete(Wraps.<DoctorCustomGroupPatient>lbQ()
                .eq(DoctorCustomGroupPatient::getPatientId, patientId));
    }
}
