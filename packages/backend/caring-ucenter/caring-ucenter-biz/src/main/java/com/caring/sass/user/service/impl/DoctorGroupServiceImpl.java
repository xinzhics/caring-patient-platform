package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.dto.ChatClearHistory;
import com.caring.sass.user.dao.DoctorGroupMapper;
import com.caring.sass.user.dao.DoctorMapper;
import com.caring.sass.user.dao.DoctorNoReadGroupDoctorMsgMapper;
import com.caring.sass.user.dao.PatientMapper;
import com.caring.sass.user.dto.DoctorNoReadGroupDoctorMsgDto;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.DoctorGroup;
import com.caring.sass.user.entity.DoctorNoReadGroupDoctorMsg;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DoctorGroupServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/9/14 16:51
 * @Version 1.0
 */
@Slf4j
@Service
public class DoctorGroupServiceImpl extends SuperServiceImpl<DoctorGroupMapper, DoctorGroup> implements DoctorGroupService {

    @Autowired
    DoctorGroupMapper doctorGroupMapper;

    @Autowired
    private DoctorNoReadGroupDoctorMsgMapper doctorNoReadGroupDoctorMsgMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    ImApi imApi;

    /**
     * 获取医生所在小组内的 所有未退出登录的医生
     * @param doctorId
     * @return
     */
    @Override
    public List<Doctor> getGroupDoctorListReadMyMessage(Long doctorId) {
        Doctor doctor = doctorMapper.selectById(doctorId);
        List<Doctor> doctors = new ArrayList<>();
        if (Objects.isNull(doctor)) {
            return null;
        }
        if (doctor.getIndependence() == 1) {
            doctors.add(doctor);
            return doctors;
        } else {
            doctors.add(doctor);
        }
        List<Doctor> msgDoctors = getReadMyMsgDoctor(doctorId);
        if (CollUtil.isNotEmpty(msgDoctors)) {
            doctors.addAll(msgDoctors);
        }
        return doctors;
    }

    /**
     * 查询 哪些医生愿意接收看 我和患者的消息
     * @param doctorId
     * @return
     */
    @Override
    public List<Doctor> getReadMyMsgDoctor(Long doctorId) {

        if (Objects.isNull(doctorId)) {
            return new ArrayList<>();
        }

        // 获取 跟我同一小组下的 其他医生
        LbqWrapper<DoctorGroup> wrapper = Wraps.<DoctorGroup>lbQ()
                .select(DoctorGroup::getDoctorId)
                .apply("group_id in (select group_id from u_user_doctor_group where doctor_id = "+doctorId+")")
                .ne(DoctorGroup::getDoctorId, doctorId);
        List<DoctorGroup> doctorGroups = doctorGroupMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(doctorGroups)) {
            return new ArrayList<>();
        }

        // 获取谁不愿意看关于我患者的消息 (设置不想看我的必定是跟我同一小组的)
        LbqWrapper<DoctorNoReadGroupDoctorMsg> msgLbqWrapper = Wraps.<DoctorNoReadGroupDoctorMsg>lbQ()
                .select(DoctorNoReadGroupDoctorMsg::getDoctorId)
                .eq(DoctorNoReadGroupDoctorMsg::getNoReadGroupDoctorId, doctorId);
        List<DoctorNoReadGroupDoctorMsg> doctorMsgList = doctorNoReadGroupDoctorMsgMapper.selectList(msgLbqWrapper);
        List<Long> readMyMsgDoctorIds = new ArrayList<>(doctorGroups.size());
        if (!CollectionUtils.isEmpty(doctorMsgList)) {
            Set<Long> noReadMeMsgDoctorIds = doctorMsgList.stream().map(DoctorNoReadGroupDoctorMsg::getDoctorId).collect(Collectors.toSet());
            for (DoctorGroup doctorGroup : doctorGroups) {

                // 将 不存在      noReadMeMsgDoctorIds 中的医生 拿到。
                if (!noReadMeMsgDoctorIds.contains(doctorGroup.getDoctorId())) {
                    readMyMsgDoctorIds.add(doctorGroup.getDoctorId());
                }
            }
        } else {
            readMyMsgDoctorIds = doctorGroups.stream().map(DoctorGroup::getDoctorId).collect(Collectors.toList());
        }

        // 根据医生的id集合查询 医生的基本信息
        if (!CollectionUtils.isEmpty(readMyMsgDoctorIds)) {
            return doctorMapper.selectList(Wraps.<Doctor>lbQ()
                    .select(Doctor::getId, Doctor::getName, Doctor::getNursingId, Doctor::getAvatar,
                            Doctor::getNickName, Doctor::getOpenId, Doctor::getImAccount, Doctor::getWxStatus, Doctor::getImWxTemplateStatus, Doctor::getImMsgStatus)
                    .in(SuperEntity::getId, readMyMsgDoctorIds));
        }
        return new ArrayList<>();
    }


    /**
     *
     * @param doctorList 医生中不包含自己
     * @param doctorId
     * @return
     */
    @Override
    public List<DoctorNoReadGroupDoctorMsgDto> setReadStauts(List<Doctor> doctorList, Long doctorId) {

        List<DoctorNoReadGroupDoctorMsgDto> doctorMsgDtos = new ArrayList<>(doctorList.size() + 1);
        Set<Long> noReadGroupDoctorIdSet;
        // 查询医生设置的 不看哪些医生患者消息
        List<DoctorNoReadGroupDoctorMsg> groupDoctorMsg = getDoctorNoReadGroupDoctorMsg(doctorId);
        if (CollectionUtils.isEmpty(groupDoctorMsg)) {
            noReadGroupDoctorIdSet = new HashSet<>();
        } else {
            noReadGroupDoctorIdSet = groupDoctorMsg.stream().map(DoctorNoReadGroupDoctorMsg::getNoReadGroupDoctorId).collect(Collectors.toSet());
        }

        // 判断 医生是否 把自己患者的消息也设置不见
        setReadStauts(doctorMsgDtos, noReadGroupDoctorIdSet, doctorId, "我");
        if (CollectionUtils.isEmpty(doctorList)) {
            return doctorMsgDtos;
        }
        for (Doctor doctor : doctorList) {

            setReadStauts(doctorMsgDtos, noReadGroupDoctorIdSet, doctor.getId(), doctor.getName());
        }
        return doctorMsgDtos;
    }

    /**
     * 设置 哪些医生 被设置了 不看患者消息
     * @param doctorMsgDtos
     * @param noReadGroupDoctorIdSet
     * @param doctorId
     * @param name
     */
    private void setReadStauts(List<DoctorNoReadGroupDoctorMsgDto> doctorMsgDtos,
                               Set<Long> noReadGroupDoctorIdSet,
                               Long doctorId, String name) {
        DoctorNoReadGroupDoctorMsgDto.DoctorNoReadGroupDoctorMsgDtoBuilder builder = DoctorNoReadGroupDoctorMsgDto.builder()
                .doctorId(doctorId)
                .name(name);

        // 如果医生ID存在 在医生设置的不看消息名单中，则设置 读取消息状态为 false
        if (noReadGroupDoctorIdSet.contains(doctorId)) {
            doctorMsgDtos.add(builder.readMsgStatus(false).build());
        } else {
            doctorMsgDtos.add(builder.readMsgStatus(true).build());
        }
    }

    /**
     * 查询医生 设置了不看哪些医生的患者 记录
     * @param doctorId
     * @return
     */
    public List<DoctorNoReadGroupDoctorMsg> getDoctorNoReadGroupDoctorMsg(Long doctorId) {
        LbqWrapper<DoctorNoReadGroupDoctorMsg> wrapper = Wraps.<DoctorNoReadGroupDoctorMsg>lbQ()
                .eq(DoctorNoReadGroupDoctorMsg::getDoctorId, doctorId)
                .select(DoctorNoReadGroupDoctorMsg::getNoReadGroupDoctorId);
        return doctorNoReadGroupDoctorMsgMapper.selectList(wrapper);

    }

    /**
     * 添加医生不想查看其它医生 的患者消息
     * 如果 医生之前已经设置过了。则会清除
     * @param groupDoctorMsg
     * @return
     */
    @Override
    public Boolean saveDoctorNoReadGroupDoctor(DoctorNoReadGroupDoctorMsg groupDoctorMsg) {

        Long doctorId = groupDoctorMsg.getDoctorId();
        Long noReadGroupDoctorId = groupDoctorMsg.getNoReadGroupDoctorId();
        if (doctorId == null ||
                noReadGroupDoctorId == null) {
            throw new BizException("参数异常。");
        }
        // 查询当前医生所在的小组关系
        DoctorGroup doctorGroup = doctorGroupMapper.selectOne(Wraps.<DoctorGroup>lbQ().eq(DoctorGroup::getDoctorId, doctorId).last(" limit 0,1 "));
        if (Objects.isNull(doctorGroup)) {
            return true;
        }
        Long groupId = doctorGroup.getGroupId();
        // 查询目标医生和这个小组是否还存在关系
        DoctorGroup doctorGroup1 = doctorGroupMapper.selectOne(Wraps.<DoctorGroup>lbQ()
                .eq(DoctorGroup::getDoctorId, noReadGroupDoctorId)
                .eq(DoctorGroup::getGroupId, groupId)
                .last(" limit 0,1 "));
        // 目标医生和小组不存在关系了。
        if (Objects.isNull(doctorGroup1)) {
            // 目标医生已经不存在于小组内。没有设置的必要了。
            String tenant = BaseContextHandler.getTenant();
            SaasGlobalThreadPool.execute(() -> cleanNoReadGroupDoctorMsg(doctorId,
                    noReadGroupDoctorId, tenant));
            throw new BizException("医生已经不在此小组内");
        }
        LbqWrapper<DoctorNoReadGroupDoctorMsg> wrapper = Wraps.<DoctorNoReadGroupDoctorMsg>lbQ()
                .eq(DoctorNoReadGroupDoctorMsg::getDoctorId, doctorId)
                .eq(DoctorNoReadGroupDoctorMsg::getNoReadGroupDoctorId, noReadGroupDoctorId);
        Integer integer = doctorNoReadGroupDoctorMsgMapper.selectCount(wrapper);
        if (integer > 0) {
            doctorNoReadGroupDoctorMsgMapper.delete(wrapper);
        } else {

            // 异步 清除 医生和 不想看医生 的患者之间产生的未读消息记录
            String tenant = BaseContextHandler.getTenant();
            SaasGlobalThreadPool.execute(() -> cleanNoReadGroupDoctorMsg(doctorId,
                    noReadGroupDoctorId, tenant));

            doctorNoReadGroupDoctorMsgMapper.insert(groupDoctorMsg);
        }
        return true;
    }

    /**
     * 清除 医生和 不想看医生 的患者之间产生的未读消息记录
     * @param doctorId 我自己
     * @param noReadGroupDoctorId 查询此医生下的患者， 使用患者的IMAccount 和 doctorId 清除未读消息
     */
    public Boolean cleanNoReadGroupDoctorMsg(Long doctorId, Long noReadGroupDoctorId, String tenantCode) {
        if (noReadGroupDoctorId == null || doctorId == null) {
            return true;
        }
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<Patient> wrapper = Wraps.<Patient>lbQ().eq(Patient::getDoctorId, noReadGroupDoctorId)
                .select(Patient::getImAccount);
        List<Patient> patients = patientMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(patients)) {
            return true;
        }
        List<String> imAccounts = new ArrayList<>(patients.size());
        for (Patient patient : patients) {
            if (!StringUtils.isEmpty(patient.getImAccount())) {
                imAccounts.add(patient.getImAccount());
            }
        }
        if (CollectionUtils.isEmpty(imAccounts)) {
            return true;
        }
        ChatClearHistory chatClearHistory = new ChatClearHistory();
        chatClearHistory.setGroupIds(imAccounts);
        chatClearHistory.setUserId(doctorId);
        chatClearHistory.setRoleType(UserType.UCENTER_DOCTOR);
        imApi.clearChatNoReadHistory(chatClearHistory);
        return true;
    }

    /**
     * 获取医生 要看哪些医生患者的聊天
     * @param doctorId
     * @return
     */
    @Override
    public List<DoctorGroup> getDoctorGroupOtherDoctor(Long doctorId) {

        LbqWrapper<DoctorGroup> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(DoctorGroup::getDoctorId, doctorId);
        lbqWrapper.select(DoctorGroup::getDoctorId, DoctorGroup::getGroupId, DoctorGroup::getJoinGroupTime);
        // 医生自己所在的小组
        List<DoctorGroup> doctorGroups = doctorGroupMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(doctorGroups)) {
            // 医生没有小组。 医生是个 独立医生
            return null;
        }
        Map<Long, LocalDateTime> doctorJoinGroupTime = doctorGroups.stream()
                .collect(Collectors.toMap(DoctorGroup::getGroupId, DoctorGroup::getJoinGroupTime, (o1, o2) -> o1));

        // 查询医生设置了 不看哪些医生
        List<DoctorNoReadGroupDoctorMsg> noReadGroupDoctorMsgs = doctorNoReadGroupDoctorMsgMapper.selectList(Wraps.<DoctorNoReadGroupDoctorMsg>lbQ()
                .select(DoctorNoReadGroupDoctorMsg::getDoctorId, DoctorNoReadGroupDoctorMsg::getNoReadGroupDoctorId)
                .eq(DoctorNoReadGroupDoctorMsg::getDoctorId, doctorId));
        Set<Long> noReadGroupDoctorMsgsSet = noReadGroupDoctorMsgs.stream()
                .map(DoctorNoReadGroupDoctorMsg::getNoReadGroupDoctorId).collect(Collectors.toSet());

        // 查询医生所在小组的 医生id 包含他自己
        List<Long> groupIds = doctorGroups.stream().map(DoctorGroup::getGroupId).collect(Collectors.toList());
        lbqWrapper = new LbqWrapper<>();
        lbqWrapper.in(DoctorGroup::getGroupId, groupIds);

        List<DoctorGroup> doctorGroupList = doctorGroupMapper.selectList(lbqWrapper);

        // 通过 不看医生 和 入组时间，查看医生愿意查看的 医患消息 和 最早可见消息
        List<DoctorGroup> result = new ArrayList<>(doctorGroupList.size());
        for (DoctorGroup group : doctorGroupList) {
            // 把设置不可见的医生给踢出去。
            if (noReadGroupDoctorMsgsSet.contains(group.getDoctorId())) {
                continue;
            }
            LocalDateTime dateTime = doctorJoinGroupTime.get(group.getGroupId());
            group.setJoinGroupTime(dateTime);
            result.add(group);
        }
        return result;


    }

    /**
     * 删除医生 和小组的关系。并清除 医生设置不看的医生记录
     * @param doctorId
     */
    @Override
    public void removeByDoctorId(Long doctorId) {

        baseMapper.delete(Wraps.<DoctorGroup>lbQ().eq(DoctorGroup::getDoctorId, doctorId));
        removeNotReadMessage(doctorId);

    }

    /**
     * 移除 医生设置的不想看某医生的设置。
     * 移除 别的医生设置的不想看 此医生的设置
     * @param doctorId
     */
    @Override
    public void removeNotReadMessage(Long doctorId) {
        doctorNoReadGroupDoctorMsgMapper.delete(Wraps.<DoctorNoReadGroupDoctorMsg>lbQ().eq(DoctorNoReadGroupDoctorMsg::getDoctorId, doctorId));
        doctorNoReadGroupDoctorMsgMapper.delete(Wraps.<DoctorNoReadGroupDoctorMsg>lbQ().eq(DoctorNoReadGroupDoctorMsg::getNoReadGroupDoctorId, doctorId));

    }


    /**
     * 查询医生所在小组的所有医生ID
     * @param doctorId
     * @return
     */
    @Override
    public List<Long> findGroupDoctorIdByDoctorId(Long doctorId) {

        List<DoctorGroup> doctorGroups = baseMapper.selectList(Wraps.<DoctorGroup>lbQ().eq(DoctorGroup::getDoctorId, doctorId).last(" limit 0,1 "));
        if (CollUtil.isEmpty(doctorGroups)) {
            return ListUtil.toList(doctorId);
        }
        DoctorGroup doctorGroup = doctorGroups.get(0);
        List<DoctorGroup> doctorGroupList = baseMapper.selectList(Wraps.<DoctorGroup>lbQ().eq(DoctorGroup::getGroupId, doctorGroup.getGroupId()).select(DoctorGroup::getGroupId, DoctorGroup::getDoctorId));
        return doctorGroupList.stream().map(DoctorGroup::getDoctorId).collect(Collectors.toList());
    }
}
