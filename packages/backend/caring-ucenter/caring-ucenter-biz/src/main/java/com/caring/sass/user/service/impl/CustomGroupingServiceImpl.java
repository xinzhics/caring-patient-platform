package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.LetterUtil;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.user.dao.CustomGroupingMapper;
import com.caring.sass.user.dao.CustomGroupingPatientMapper;
import com.caring.sass.user.dao.PatientMapper;
import com.caring.sass.user.dto.DoctorCustomGroupPatientModel;
import com.caring.sass.user.dto.NursingCustomGroupPatientPageDTO;
import com.caring.sass.user.dto.NursingCustomGroupUpdateDTO;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.CustomGroupingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName CustomGroupingServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/8/27 11:55
 * @Version 1.0
 */
@Service
@Slf4j
public class CustomGroupingServiceImpl  extends SuperServiceImpl<CustomGroupingMapper, CustomGrouping> implements CustomGroupingService {

    @Autowired
    CustomGroupingPatientMapper customGroupingPatientMapper;

    @Autowired
    PatientMapper patientMapper;

    /**
     * 查询医助的患者 所在的 自定义小组
     * @param patientId
     * @param userId
     * @return
     */
    @Override
    public List<CustomGrouping> findPatientInGroupList(Long patientId, Long userId) {

        List<CustomGroupingPatient> patientList = customGroupingPatientMapper.selectList(Wraps.<CustomGroupingPatient>lbQ().eq(CustomGroupingPatient::getPatientId, patientId));
        if (CollUtil.isEmpty(patientList)) {
            List<Long> groupIds = patientList.stream().map(CustomGroupingPatient::getCustomGroupingId).collect(Collectors.toList());
            return baseMapper.selectList(Wraps.<CustomGrouping>lbQ().eq(CustomGrouping::getUserId, userId).in(SuperEntity::getId, groupIds).eq(CustomGrouping::getRoleType, UserType.UCENTER_NURSING_STAFF));
        }

        return new ArrayList<>();
    }

    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(CustomGrouping model) {


        int insert = baseMapper.insert(model);
        List<Long> patientIds = model.getPatientIds();
        List<CustomGroupingPatient> list;
        Long id = model.getId();
        if (insert > 0) {
            if (CollectionUtils.isNotEmpty(patientIds)) {
                list = new ArrayList<>(patientIds.size());
                for (Long patientId : patientIds) {
                    list.add(CustomGroupingPatient.builder().customGroupingId(id).patientId(patientId).build());
                }
                customGroupingPatientMapper.insertBatchSomeColumn(list);
            } else {
                String patientIdStrings = model.getPatientIdStrings();
                if (StringUtils.isNotEmptyString(patientIdStrings)) {
                    String[] split = patientIdStrings.split(",");
                    list = new ArrayList<>(split.length);
                    for (String s : split) {
                        list.add(CustomGroupingPatient.builder().customGroupingId(id).patientId(Long.parseLong(s)).build());
                    }
                    customGroupingPatientMapper.insertBatchSomeColumn(list);
                }
            }
        }
        return true;
    }

    /**
     * 删除患者时，将患者从小组 关系中删除
     * @param patientId
     */
    @Override
    public void removeByPatientId(Long patientId) {
        if (Objects.nonNull(patientId)) {
            customGroupingPatientMapper.delete(Wraps.<CustomGroupingPatient>lbQ().eq(CustomGroupingPatient::getPatientId, patientId));
        }
    }

    /**
     * 并统计小组人员的数量
     * @param queryWrapper
     * @return
     */
    @Override
    public List<CustomGrouping> list(Wrapper<CustomGrouping> queryWrapper) {

        List<CustomGrouping> groupingList = baseMapper.selectList(queryWrapper);

        if (groupingList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> longList = groupingList.stream().map(SuperEntity::getId).collect(Collectors.toList());

        QueryWrapper<CustomGroupingPatient> wrapper = Wrappers.<CustomGroupingPatient>query()
                .select("custom_grouping_id as customGroupingId", "count(*) as total")
                .in("custom_grouping_id", longList)
                .groupBy("custom_grouping_id");

        List<Map<String, Object>> mapList = customGroupingPatientMapper.selectMaps(wrapper);
        Map<Long, Integer> totalMap = new HashMap<>(groupingList.size());
        for (Map<String, Object> map : mapList) {
            Long groupingId = Convert.toLong(map.get("customGroupingId"));
            Integer total = Convert.toInt(map.get("total"));
            if (Objects.nonNull(groupingId) && Objects.nonNull(total)) {
                totalMap.put(groupingId, total);
            }
        }
        for (CustomGrouping grouping : groupingList) {
            Integer integer = totalMap.get(grouping.getId());
            if (integer != null) {
                grouping.setPatientCount(integer);
            }
        }
        return groupingList;
    }

    /**
     * 查询医助所有的小组。并统计小组内未取关的患者的数量
     * @param nursingId
     * @param patientStatus
     * @return
     */
    @Override
    public List<CustomGrouping> queryAllAndCountPatientNumber(Long nursingId, List<Integer> patientStatus) {
        LbqWrapper<CustomGrouping> lbqWrapper = Wraps.<CustomGrouping>lbQ()
                .eq(CustomGrouping::getUserId, nursingId)
                .eq(CustomGrouping::getRoleType, UserType.UCENTER_NURSING_STAFF);
        List<CustomGrouping> groupingList = baseMapper.selectList(lbqWrapper);

        if (groupingList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> longList = groupingList.stream().map(SuperEntity::getId).collect(Collectors.toList());

        QueryWrapper<CustomGroupingPatient> wrapper = Wrappers.<CustomGroupingPatient>query()
                .select("custom_grouping_id as customGroupingId", "count(*) as total")
                .in("custom_grouping_id", longList)
                .groupBy("custom_grouping_id");

        if (CollUtil.isNotEmpty(patientStatus)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer status : patientStatus) {
                stringBuilder.append(status).append(",");
            }
            String statusString = stringBuilder.toString();
            String substring = statusString.substring(0, statusString.length() - 1);
            wrapper.apply(" patient_id in (select id from u_user_patient where service_advisor_id = '" + nursingId + "' and status_ in (" + substring + "))");
        }

        List<Map<String, Object>> mapList = customGroupingPatientMapper.selectMaps(wrapper);
        Map<Long, Integer> totalMap = new HashMap<>(groupingList.size());
        for (Map<String, Object> map : mapList) {
            Long groupingId = Convert.toLong(map.get("customGroupingId"));
            Integer total = Convert.toInt(map.get("total"));
            if (Objects.nonNull(groupingId) && Objects.nonNull(total)) {
                totalMap.put(groupingId, total);
            }
        }
        for (CustomGrouping grouping : groupingList) {
            Integer integer = totalMap.get(grouping.getId());
            if (integer != null) {
                grouping.setPatientCount(integer);
            }
        }
        return groupingList;
    }


    /**
     * 新医助端 新增自定义小组的方法
     *
     * @param groupUpdateDTO
     * @return
     */
    @Transactional
    @Override
    public CustomGrouping createGroupPatient(NursingCustomGroupUpdateDTO groupUpdateDTO) {
        CustomGrouping customGrouping = new CustomGrouping();
        customGrouping.setName(groupUpdateDTO.getName());
        customGrouping.setUserId(groupUpdateDTO.getUserId());
        customGrouping.setRoleType(UserType.UCENTER_NURSING_STAFF);
        customGrouping.setGroupSort(0);
        baseMapper.insert(customGrouping);
        Long groupingId = customGrouping.getId();
        Set<Long> joinPatientIds = groupUpdateDTO.getJoinPatientIds();
        if (CollUtil.isNotEmpty(joinPatientIds)) {
            List<CustomGroupingPatient> customGroupingPatients = new ArrayList<>(joinPatientIds.size());
            for (Long patientId : joinPatientIds) {
                customGroupingPatients.add(
                        CustomGroupingPatient.builder()
                                .patientId(patientId)
                                .customGroupingId(groupingId)
                                .build());
            }
            customGroupingPatientMapper.insertBatchSomeColumn(customGroupingPatients);
        }
        return customGrouping;
    }

    /**
     * 新医助端 修改自定义小组的方法
     *
     * @param customGroupId
     * @param groupUpdateDTO
     * @return
     */
    @Transactional
    @Override
    public CustomGrouping updateGroupPatient(Long customGroupId, NursingCustomGroupUpdateDTO groupUpdateDTO) {
        String groupName = groupUpdateDTO.getName();
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
            customGroupingPatientMapper.delete(Wraps.<CustomGroupingPatient>lbQ()
                    .eq(CustomGroupingPatient::getCustomGroupingId, customGroupId)
                    .in(CustomGroupingPatient::getPatientId, joinPatientIds));
            List<CustomGroupingPatient> customGroupingPatients = new ArrayList<>(joinPatientIds.size());
            for (Long patientId : joinPatientIds) {
                customGroupingPatients.add(
                        CustomGroupingPatient.builder()
                                .patientId(patientId)
                                .customGroupingId(customGroupId)
                                .build());
            }
            customGroupingPatientMapper.insertBatchSomeColumn(customGroupingPatients);
        }
        if (CollUtil.isNotEmpty(removePatientIds)) {
            customGroupingPatientMapper.delete(Wraps.<CustomGroupingPatient>lbQ()
                    .eq(CustomGroupingPatient::getCustomGroupingId, customGroupId)
                    .in(CustomGroupingPatient::getPatientId, removePatientIds));
        }
        CustomGrouping customGrouping = new CustomGrouping();
        customGrouping.setId(customGroupId);
        customGrouping.setName(groupName);
        super.updateById(customGrouping);
        return customGrouping;
    }

    /**
     * 旧的医助端使用的这个更新
     * @param model
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(CustomGrouping model) {

        List<Long> patientIds = model.getPatientIds();
        customGroupingPatientMapper.delete(Wraps.<CustomGroupingPatient>lbQ().eq(CustomGroupingPatient::getCustomGroupingId, model.getId()));
        List<CustomGroupingPatient> list;
        Long modelId = model.getId();
        if (CollectionUtils.isNotEmpty(patientIds)) {
            list = new ArrayList<>(patientIds.size());
            for (Long patientId : patientIds) {
                list.add(CustomGroupingPatient.builder().customGroupingId(modelId).patientId(patientId).build());
            }
            customGroupingPatientMapper.insertBatchSomeColumn(list);
        } else {
            String patientIdStrings = model.getPatientIdStrings();
            if (StringUtils.isNotEmptyString(patientIdStrings)) {
                String[] split = patientIdStrings.split(",");
                list = new ArrayList<>(split.length);
                for (String s : split) {
                    list.add(CustomGroupingPatient.builder().customGroupingId(modelId).patientId(Long.parseLong(s)).build());
                }
                customGroupingPatientMapper.insertBatchSomeColumn(list);
            }
        }
        return super.updateById(model);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {

        customGroupingPatientMapper.delete(Wraps.<CustomGroupingPatient>lbQ().eq(CustomGroupingPatient::getCustomGroupingId, id));
        baseMapper.deleteById(id);
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        customGroupingPatientMapper.delete(Wraps.<CustomGroupingPatient>lbQ().in(CustomGroupingPatient::getCustomGroupingId, idList));
        baseMapper.deleteBatchIds(idList);
        return true;
    }

    @Override
    public List<Patient> findAllPatientBaseInfo(Long customGroupingId) {

        LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ().select(SuperEntity::getId, Patient::getName,
                Patient::getImAccount, Patient::getAvatar, Patient::getRemark, Patient::getDoctorRemark)
                .apply(" id in (select patient_id from u_user_custom_grouping_patient where custom_grouping_id = " + customGroupingId + ") ")
                .orderByDesc(SuperEntity::getCreateTime);
        return patientMapper.selectList(lbqWrapper);
    }


    @Override
    public IPage<Patient> findPatientBaseInfo(IPage buildPage, Long customGroupingId) {


        LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ().select(SuperEntity::getId, Patient::getName,
                Patient::getImAccount, Patient::getAvatar, Patient::getRemark, Patient::getDoctorRemark)
                .apply(" id in (select patient_id from u_user_custom_grouping_patient where custom_grouping_id = " + customGroupingId + ") ")
                .orderByDesc(SuperEntity::getCreateTime);
        return patientMapper.selectPage(buildPage, lbqWrapper);
    }

    @Override
    public IPage<JSONObject> pageNursingPatient(IPage<Patient> iPage, NursingCustomGroupPatientPageDTO model) {
        Long nursingId = model.getNursingId();
        LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ()
                .eq(Patient::getServiceAdvisorId, nursingId)
                .select(SuperEntity::getId, Patient::getName, Patient::getDiagnosisName,
                        Patient::getImAccount, Patient::getAvatar, Patient::getRemark,
                        Patient::getDoctorRemark, Patient::getNameFirstLetter)
                .orderByAsc(Patient::getNameFirstLetter);
        Long customGroupId = model.getCustomGroupId();
        String patientName = model.getPatientName();
        if (StrUtil.isNotEmpty(patientName)) {
            lbqWrapper.like(Patient::getName, patientName);
        }
        patientMapper.selectPage(iPage, lbqWrapper);
        List<Patient> records = iPage.getRecords();
        IPage<JSONObject> resultPage = new Page<>();
        resultPage.setSize(iPage.getSize());
        resultPage.setTotal(iPage.getTotal());
        resultPage.setCurrent(iPage.getCurrent());
        resultPage.setPages(iPage.getPages());
        if (CollUtil.isEmpty(records)) {
            return resultPage;
        }

        List<Long> patientIds = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
        List<CustomGroupingPatient> groupPatients = new ArrayList<>();
        if (Objects.nonNull(customGroupId)) {
            groupPatients = customGroupingPatientMapper.selectList(Wraps.<CustomGroupingPatient>lbQ()
                    .eq(CustomGroupingPatient::getCustomGroupingId, customGroupId)
                    .in(CustomGroupingPatient::getPatientId, patientIds));
        }
        if (CollUtil.isEmpty(groupPatients)) {
            groupPatients = new ArrayList<>();
        }
        List<DoctorCustomGroupPatientModel> patientModels = new ArrayList<>(records.size());
        final Set<Long> checkedPatientIdSet = groupPatients.stream().map(CustomGroupingPatient::getPatientId).collect(Collectors.toSet());
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

    @Override
    public void deletePatient(Long customGroupingId, Long patientId) {

        LbqWrapper<CustomGroupingPatient> wrapper = Wraps.<CustomGroupingPatient>lbQ().eq(CustomGroupingPatient::getCustomGroupingId, customGroupingId)
                .eq(CustomGroupingPatient::getPatientId, patientId);
        customGroupingPatientMapper.delete(wrapper);

    }
}
